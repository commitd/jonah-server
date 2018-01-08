package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.data.elasticsearch.providers.ElasticsearchEntityProvider;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class EsEntityProviderFactory
    extends AbstractElasticsearchDataProviderFactory<EntityProvider> {

  private final ObjectMapper mapper;


  public EsEntityProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-entities", EntityProvider.class);
    this.mapper = mapper;
  }


  @Override
  public Mono<EntityProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final ElasticsearchTemplate elastic = buildElasticTemplate(settings);

      final EsEntityService service = new EsEntityService(mapper, elastic);

      return Mono.just(new ElasticsearchEntityProvider(dataset, datasource, service));
    } catch (final Exception e) {
      log.error("Unable to create ES Document Provider", e);
      return Mono.empty();
    }
  }

}
