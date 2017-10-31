package io.committed.ketos.plugins.data.configurer.mongo;

import java.util.Map;

import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;

import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.plugins.data.mongo.providers.MongoMentionProvider;
import io.committed.ketos.plugins.data.mongo.repository.BaleenEntitiesRepository;
import reactor.core.publisher.Mono;

public class MongoMentionProviderFactory
    extends AbstractMongoDataProviderFactory<MentionProvider> {

  public MongoMentionProviderFactory() {
    super("baleen-mongo-mentions", MentionProvider.class);
  }

  @Override
  public Mono<MentionProvider> build(final String corpus, final Map<String, Object> settings) {
    final ReactiveRepositoryFactorySupport support = buildRepositoryFactory(settings);

    final BaleenEntitiesRepository repository =
        support.getRepository(BaleenEntitiesRepository.class);

    return Mono.just(new MongoMentionProvider(corpus, repository));
  }

}
