package io.committed.ketos.plugins.graphql.feedback;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.committed.invest.extensions.InvestGraphQlExtension;
import io.committed.invest.extensions.InvestUiExtension;
import io.committed.invest.server.data.services.DatasetProviders;

@Configuration
public class FeedbackGraphQlExtension implements InvestGraphQlExtension {


  @Bean
  public FeedbackGraphQlService feedbackGraphQlService(
      @Autowired(required = false) final DatasetProviders providers,
      final List<InvestUiExtension> uiExtensions) {
    return new FeedbackGraphQlService(providers, uiExtensions);
  }
}
