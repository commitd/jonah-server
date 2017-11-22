package io.committed.ketos.plugins.data.mongo.factory;

import java.util.Map;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;

import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.plugins.data.mongo.providers.MongoEntityProvider;
import io.committed.ketos.plugins.data.mongo.repository.BaleenEntitiesRepository;
import reactor.core.publisher.Mono;

public class MongoEntityProviderFactory
    extends AbstractMongoDataProviderFactory<EntityProvider> {

  public MongoEntityProviderFactory() {
    super("baleen-mongo-entities", EntityProvider.class);
  }

  @Override
  public Mono<EntityProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final ReactiveMongoTemplate mongoTemplate = buildMongoTemplate(settings);
    final ReactiveRepositoryFactorySupport support =
        buildRepositoryFactory(mongoTemplate);

    final BaleenEntitiesRepository repository =
        support.getRepository(BaleenEntitiesRepository.class);

    return Mono.just(new MongoEntityProvider(dataset, datasource, mongoTemplate, repository));
  }

}
