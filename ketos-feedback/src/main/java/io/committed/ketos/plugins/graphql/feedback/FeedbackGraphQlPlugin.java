package io.committed.ketos.plugins.graphql.feedback;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.committed.vessel.extensions.VesselGraphQlExtension;
import io.committed.vessel.extensions.VesselUiExtension;
import io.committed.vessel.server.data.services.DatasetProviders;

@Configuration
public class FeedbackGraphQlPlugin implements VesselGraphQlExtension {


  @Bean
  public FeedbackGraphQlService feedbackGraphQlService(
      @Autowired(required = false) final DatasetProviders providers,
      final List<VesselUiExtension> uiExtensions) {
    return new FeedbackGraphQlService(providers, uiExtensions);
  }
}
