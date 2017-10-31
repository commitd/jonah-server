package io.committed.ketos.plugins.data.configurer.mongo;

import java.util.Map;

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
  public Mono<EntityProvider> build(final String corpus, final Map<String, Object> settings) {
    final ReactiveRepositoryFactorySupport support = buildRepositoryFactory(settings);

    final BaleenEntitiesRepository repository =
        support.getRepository(BaleenEntitiesRepository.class);

    return Mono.just(new MongoEntityProvider(corpus, repository));
  }

}
