package io.committed.ketos.plugins.data.mongo.factory;

import java.util.Map;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
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
  public Mono<MentionProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final ReactiveMongoTemplate mongoTemplate = buildMongoTemplate(settings);
    final ReactiveRepositoryFactorySupport support =
        buildRepositoryFactory(mongoTemplate);

    final BaleenEntitiesRepository repository =
        support.getRepository(BaleenEntitiesRepository.class);

    return Mono.just(new MongoMentionProvider(dataset, datasource, repository));
  }

}
