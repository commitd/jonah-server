package io.committed.ketos.plugins.data.feedback.mongo;

import java.util.Map;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import io.committed.invest.server.data.providers.AbstractDataProviderFactory;
import io.committed.invest.server.data.providers.DatabaseConstants;
import io.committed.ketos.plugins.data.feedback.data.FeedbackDataProvider;
import reactor.core.publisher.Mono;

public class MongoFeedbackProviderFactory
    extends AbstractDataProviderFactory<FeedbackDataProvider> {

  public MongoFeedbackProviderFactory() {
    super("feedback-mongo", FeedbackDataProvider.class, DatabaseConstants.MONGO);
  }

  @Override
  public Mono<FeedbackDataProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    // TODO move the functionality from the ketos abstract mongo factories to invest-data-mongo

    final MongoClient mongoClient = MongoClients.create();
    final ReactiveMongoTemplate mongoTemplate = new ReactiveMongoTemplate(mongoClient, "feedback");
    final ReactiveMongoRepositoryFactory factory =
        new ReactiveMongoRepositoryFactory(mongoTemplate);
    final MongoFeedbackRepository repository = factory.getRepository(MongoFeedbackRepository.class);
    return Mono.just(new MongoFeedbackDataProvider(dataset, datasource, repository));
  }

}
