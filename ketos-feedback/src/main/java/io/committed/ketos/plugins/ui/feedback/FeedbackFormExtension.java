package io.committed.ketos.plugins.ui.feedback;

import org.springframework.context.annotation.Configuration;

import io.committed.invest.extensions.InvestUiExtension;

@Configuration
public class FeedbackFormExtension implements InvestUiExtension {

  @Override
  public String getId() {
    return "feedback-form";
  }

  @Override
  public String getName() {
    return "Feedback";
  }

  @Override
  public String getDescription() {
    return "Leave comments on existing functions or request new features";
  }

}
