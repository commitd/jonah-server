package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.elasticsearch.client.Client;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.constants.BaleenElasticsearchConstants;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.data.elasticsearch.providers.ElasticsearchEntityProvider;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class EsEntityProviderFactory
    extends AbstractElasticsearchDataProviderFactory<EntityProvider> {

  private final ObjectMapper mapper;


  public EsEntityProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-entities", EntityProvider.class, BaleenElasticsearchConstants.DEFAULT_INDEX,
        BaleenElasticsearchConstants.DEFAULT_ENTITY_TYPE);
    this.mapper = mapper;
  }


  @Override
  public Mono<EntityProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final Client elastic = buildElasticClient(settings);

      final EsEntityService service =
          new EsEntityService(elastic, mapper, getIndexName(settings), getTypeName(settings));

      return Mono.just(new ElasticsearchEntityProvider(dataset, datasource, service));

    } catch (final Exception e) {
      log.error("Unable to create ES Entity Provider", e);
      return Mono.empty();
    }
  }

}
