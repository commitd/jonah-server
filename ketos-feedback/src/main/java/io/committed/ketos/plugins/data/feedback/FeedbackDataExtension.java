package io.committed.ketos.plugins.data.feedback;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.committed.invest.extensions.InvestDataExtension;
import io.committed.ketos.plugins.data.feedback.mongo.MongoFeedbackProviderFactory;

@Configuration
public class FeedbackDataExtension implements InvestDataExtension {


  @Bean
  public MongoFeedbackProviderFactory mongoFeedbackProviderFactory() {
    return new MongoFeedbackProviderFactory();
  }
}
