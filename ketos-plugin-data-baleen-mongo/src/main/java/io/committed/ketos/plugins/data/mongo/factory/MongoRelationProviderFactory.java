package io.committed.ketos.plugins.data.mongo.factory;

import java.util.Map;

import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;

import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.plugins.data.mongo.providers.MongoRelationProvider;
import io.committed.ketos.plugins.data.mongo.repository.BaleenRelationRepository;
import reactor.core.publisher.Mono;

public class MongoRelationProviderFactory
    extends AbstractMongoDataProviderFactory<RelationProvider> {

  public MongoRelationProviderFactory() {
    super("baleen-mongo-relations", RelationProvider.class);
  }

  @Override
  public Mono<RelationProvider> build(final String dataset, final String datasoruce,
      final Map<String, Object> settings) {
    final ReactiveRepositoryFactorySupport support = buildRepositoryFactory(settings);

    final BaleenRelationRepository repository =
        support.getRepository(BaleenRelationRepository.class);

    return Mono.just(new MongoRelationProvider(dataset, datasoruce, repository));
  }

}
