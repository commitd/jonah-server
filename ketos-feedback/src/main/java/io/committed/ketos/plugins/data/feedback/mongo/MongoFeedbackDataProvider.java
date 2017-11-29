package io.committed.ketos.plugins.data.feedback.mongo;

import io.committed.ketos.plugins.data.feedback.data.Feedback;
import io.committed.ketos.plugins.data.feedback.data.FeedbackDataProvider;
import io.committed.vessel.server.data.providers.AbstractDataProvider;
import io.committed.vessel.server.data.providers.DatabaseConstants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoFeedbackDataProvider extends AbstractDataProvider
    implements FeedbackDataProvider {

  private final MongoFeedbackRepository repository;

  protected MongoFeedbackDataProvider(final String dataset, final String datasource,
      final MongoFeedbackRepository repository) {
    super(dataset, datasource);
    this.repository = repository;
  }

  @Override
  public Flux<Feedback> findAll(final int offset, final int limit) {
    return repository.findAll();
  }

  @Override
  public Mono<Feedback> save(final Feedback feedback) {
    return repository.save(feedback);
  }

  @Override
  public void delete(final String id) {
    repository.deleteById(id).subscribe();
  }

  @Override
  public String getDatabase() {
    return DatabaseConstants.MONGO;
  }


}
