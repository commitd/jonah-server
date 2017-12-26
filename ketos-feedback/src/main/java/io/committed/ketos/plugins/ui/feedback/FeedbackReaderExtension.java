package io.committed.ketos.plugins.ui.feedback;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.context.annotation.Configuration;

import io.committed.invest.extensions.InvestUiExtension;

@Configuration
public class FeedbackReaderExtension implements InvestUiExtension {

  @Override
  public String getId() {
    return "feedback-reader";
  }

  @Override
  public String getName() {
    return "Read feedback";
  }

  @Override
  public String getDescription() {
    return "Read feedback from users";
  }

  @Override
  public Collection<String> getRoles() {
    return Arrays.asList("ADMIN");
  }
}