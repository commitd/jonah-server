package io.committed.ketos.plugins.graphql.feedback;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.committed.invest.extensions.InvestGraphQlExtension;
import io.committed.invest.extensions.InvestUiExtension;
import io.committed.invest.extensions.data.providers.DataProviders;

/** Extension providing GraphQL resolvers for feedback. */
@Configuration
public class FeedbackGraphQlExtension implements InvestGraphQlExtension {

  @Override
  public String getName() {
    return "Feedback GraphQL";
  }

  @Override
  public String getDescription() {
    return "Feedback GraphQL Resolvers";
  }

  @Bean
  public FeedbackGraphQlService feedbackGraphQlService(
      @Autowired(required = false) final DataProviders providers,
      final List<InvestUiExtension> uiExtensions) {
    return new FeedbackGraphQlService(providers, uiExtensions);
  }
}
