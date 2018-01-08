package io.committed.ketos.plugins.data.feedback.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import io.committed.ketos.plugins.data.feedback.data.Feedback;

@NoRepositoryBean
public interface MongoFeedbackRepository extends ReactiveMongoRepository<Feedback, String> {

}
