package io.committed.ketos.plugins.data.feedback;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.committed.invest.extensions.InvestDataExtension;
import io.committed.ketos.plugins.data.feedback.mongo.MongoFeedbackProviderFactory;

/** Extension which offers Mongo data provider for feedback */
@Configuration
public class FeedbackDataExtension implements InvestDataExtension {

  @Override
  public String getName() {
    return "Feedback data";
  }

  @Override
  public String getDescription() {
    return "Feedback data providers";
  }

  @Bean
  public MongoFeedbackProviderFactory mongoFeedbackProviderFactory() {
    return new MongoFeedbackProviderFactory();
  }
}
