package io.committed.ketos.plugins.data.feedback.data;

import io.committed.invest.extensions.data.providers.DataProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FeedbackDataProvider extends DataProvider {

  @Override
  default String getProviderType() {
    return "Feedback";
  }

  Flux<Feedback> findAll(int offset, int limit);

  Flux<Feedback> findAllByUser(String user, int offset, int limit);

  Mono<Feedback> save(Feedback feedback);

  void delete(String id);

  void deleteByUser(String id, String user);

}
