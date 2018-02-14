package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class EsEntityProviderFactory
    extends AbstractElasticsearchDataProviderFactory<EntityProvider> {

  private final ObjectMapper mapper;


  public EsEntityProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-entities", EntityProvider.class, "baleen", "entity");
    this.mapper = mapper;
  }


  @Override
  public Mono<EntityProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final ElasticsearchTemplate elastic = buildElasticTemplate(settings);

      final EsEntityService service =
          new EsEntityService(mapper, elastic, getIndexName(settings), getTypeName(settings));

      // return Mono.just(new ElasticsearchEntityProvider(dataset, datasource, service));
      return Mono.empty();

    } catch (final Exception e) {
      log.error("Unable to create ES Entity Provider", e);
      return Mono.empty();
    }
  }

}
