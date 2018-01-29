package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.data.elasticsearch.providers.ElasticsearchRelationProvider;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class EsRelationProviderFactory
    extends AbstractElasticsearchDataProviderFactory<RelationProvider> {

  private final ObjectMapper mapper;


  public EsRelationProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-relations", RelationProvider.class);
    this.mapper = mapper;
  }


  @Override
  public Mono<RelationProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final ElasticsearchTemplate elastic = buildElasticTemplate(settings);

      final EsDocumentService service = new EsDocumentService(mapper, elastic);

      return Mono.just(new ElasticsearchRelationProvider(dataset, datasource, service));
    } catch (final Exception e) {
      log.error("Unable to create ES Document Provider", e);
      return Mono.empty();
    }
  }

}
