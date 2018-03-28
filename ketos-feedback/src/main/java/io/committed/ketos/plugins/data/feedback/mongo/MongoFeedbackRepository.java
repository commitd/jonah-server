package io.committed.ketos.plugins.data.feedback.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import reactor.core.publisher.Flux;

import io.committed.ketos.plugins.data.feedback.data.Feedback;

/** Spring data repository for Feedback storage and retrieval. */
@NoRepositoryBean
public interface MongoFeedbackRepository extends ReactiveMongoRepository<Feedback, String> {

  Flux<Feedback> findByUser(String user);

  Flux<Feedback> deleteByIdAndUser(String id, String user);
}
