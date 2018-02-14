package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class EsRelationProviderFactory
    extends AbstractElasticsearchDataProviderFactory<RelationProvider> {

  private final ObjectMapper mapper;


  public EsRelationProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-relations", RelationProvider.class, "baleen", "relation");
    this.mapper = mapper;
  }


  @Override
  public Mono<RelationProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final ElasticsearchTemplate elastic = buildElasticTemplate(settings);

      final EsRelationService service =
          new EsRelationService(mapper, elastic, getIndexName(settings), getTypeName(settings));

      // return Mono.just(new ElasticsearchRelationProvider(dataset, datasource, service));
      return Mono.empty();
    } catch (final Exception e) {
      log.error("Unable to create ES Relation Provider", e);
      return Mono.empty();
    }
  }

}
