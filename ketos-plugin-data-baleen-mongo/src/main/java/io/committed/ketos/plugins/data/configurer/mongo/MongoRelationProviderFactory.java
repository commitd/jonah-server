package io.committed.ketos.plugins.data.configurer.mongo;

import java.util.Map;

import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;

import io.committed.ketos.plugins.data.mongo.providers.MongoRelationProvider;
import io.committed.ketos.plugins.data.mongo.repository.BaleenRelationRepository;
import io.committed.ketos.plugins.graphql.baleenservices.providers.RelationProvider;
import reactor.core.publisher.Mono;

public class MongoRelationProviderFactory
    extends AbstractMongoDataProviderFactory<RelationProvider> {

  public MongoRelationProviderFactory() {
    super("baleen-mongo", RelationProvider.class);
  }

  @Override
  public Mono<RelationProvider> build(final String corpus, final Map<String, Object> settings) {
    final ReactiveRepositoryFactorySupport support = buildRepositoryFactory(settings);

    final BaleenRelationRepository repository =
        support.getRepository(BaleenRelationRepository.class);

    return Mono.just(new MongoRelationProvider(corpus, repository));
  }

}