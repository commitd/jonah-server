package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.elasticsearch.client.Client;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.constants.BaleenElasticsearchConstants;
import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.data.elasticsearch.providers.ElasticsearchMetadataProvider;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * A factory for creating ES MetadataProvider objects.
 */
@Slf4j
@Service
public class EsMetadataProviderFactory
    extends AbstractElasticsearchDataProviderFactory<MetadataProvider> {

  private final ObjectMapper mapper;


  public EsMetadataProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-metadata", MetadataProvider.class, BaleenElasticsearchConstants.DEFAULT_INDEX,
        BaleenElasticsearchConstants.DEFAULT_DOCUMENT_TYPE);
    this.mapper = mapper;
  }


  @Override
  public Mono<MetadataProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final Client elastic = buildElasticClient(settings);

      final EsDocumentService service =
          new EsDocumentService(elastic, mapper, getIndexName(settings), getTypeName(settings));

      return Mono.just(new ElasticsearchMetadataProvider(dataset, datasource, service));

    } catch (final Exception e) {
      log.error("Unable to create ES Metadata Provider", e);
      return Mono.empty();
    }
  }

}
