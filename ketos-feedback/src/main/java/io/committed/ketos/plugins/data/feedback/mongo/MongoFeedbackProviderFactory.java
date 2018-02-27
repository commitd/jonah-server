package io.committed.ketos.plugins.data.feedback.mongo;

import java.util.Map;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;
import io.committed.invest.support.data.mongo.AbstractSpringDataMongoDataProviderFactory;
import io.committed.ketos.plugins.data.feedback.data.FeedbackDataProvider;
import reactor.core.publisher.Mono;

public class MongoFeedbackProviderFactory
    extends AbstractSpringDataMongoDataProviderFactory<FeedbackDataProvider> {

  public MongoFeedbackProviderFactory() {
    super("feedback-mongo", FeedbackDataProvider.class);
  }

  @Override
  public Mono<FeedbackDataProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final ReactiveMongoTemplate mongoTemplate = buildMongoTemplate(settings);
    final ReactiveRepositoryFactorySupport factorySupport = buildRepositoryFactory(mongoTemplate);
    final MongoFeedbackRepository repository = factorySupport.getRepository(MongoFeedbackRepository.class);
    return Mono.just(new MongoFeedbackDataProvider(dataset, datasource, repository));
  }


}
