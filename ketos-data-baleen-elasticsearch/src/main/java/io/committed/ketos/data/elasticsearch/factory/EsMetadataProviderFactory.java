package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.data.elasticsearch.providers.ElasticsearchMetadataProvider;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class EsMetadataProviderFactory
    extends AbstractElasticsearchDataProviderFactory<MetadataProvider> {

  private final ObjectMapper mapper;


  public EsMetadataProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-metadata", MetadataProvider.class);
    this.mapper = mapper;
  }


  @Override
  public Mono<MetadataProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final ElasticsearchTemplate elastic = buildElasticTemplate(settings);

      final EsDocumentService service = new EsDocumentService(mapper, elastic);

      return Mono.just(new ElasticsearchMetadataProvider(dataset, datasource, service));
    } catch (final Exception e) {
      log.error("Unable to create ES Document Provider", e);
      return Mono.empty();
    }
  }

}
