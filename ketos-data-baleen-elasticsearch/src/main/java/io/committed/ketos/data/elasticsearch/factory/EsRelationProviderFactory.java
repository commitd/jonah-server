package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.elasticsearch.client.Client;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.constants.BaleenElasticsearchConstants;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.data.elasticsearch.providers.ElasticsearchRelationProvider;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class EsRelationProviderFactory
    extends AbstractElasticsearchDataProviderFactory<RelationProvider> {

  private final ObjectMapper mapper;


  public EsRelationProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-relations", RelationProvider.class, BaleenElasticsearchConstants.DEFAULT_INDEX,
        BaleenElasticsearchConstants.DEFAULT_RELATION_TYPE);
    this.mapper = mapper;
  }


  @Override
  public Mono<RelationProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final Client elastic = buildElasticClient(settings);

      final EsRelationService service =
          new EsRelationService(elastic, mapper, getIndexName(settings), getTypeName(settings));

      return Mono.just(new ElasticsearchRelationProvider(dataset, datasource, service));

    } catch (final Exception e) {
      log.error("Unable to create ES Relation Provider", e);
      return Mono.empty();
    }
  }

}
