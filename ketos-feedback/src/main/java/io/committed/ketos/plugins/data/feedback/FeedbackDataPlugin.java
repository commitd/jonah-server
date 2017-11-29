package io.committed.ketos.plugins.data.feedback;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.committed.ketos.plugins.data.feedback.mongo.MongoFeedbackProviderFactory;
import io.committed.vessel.extensions.VesselDataExtension;

@Configuration
public class FeedbackDataPlugin implements VesselDataExtension {


  @Bean
  public MongoFeedbackProviderFactory mongoFeedbackProviderFactory() {
    return new MongoFeedbackProviderFactory();
  }
}
