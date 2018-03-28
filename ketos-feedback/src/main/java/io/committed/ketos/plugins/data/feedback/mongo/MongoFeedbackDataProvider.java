package io.committed.ketos.plugins.data.feedback.mongo;

import io.committed.invest.extensions.data.providers.AbstractDataProvider;
import io.committed.invest.extensions.data.providers.DatabaseConstants;
import io.committed.ketos.plugins.data.feedback.data.Feedback;
import io.committed.ketos.plugins.data.feedback.data.FeedbackDataProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Mongo implementation of {@link FeedbackDataProvider}
 *
 */
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
    return repository.findAll().skip(offset).take(limit);
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

  @Override
  public Flux<Feedback> findAllByUser(final String user, final int offset, final int limit) {
    return repository.findByUser(user).skip(offset).take(limit);

  }

  @Override
  public void deleteByUser(final String id, final String user) {
    repository.deleteByIdAndUser(id, user).subscribe();
  }


}
