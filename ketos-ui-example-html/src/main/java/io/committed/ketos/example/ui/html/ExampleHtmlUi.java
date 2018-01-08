package io.committed.ketos.example.ui.html;

import org.springframework.context.annotation.Configuration;
import io.committed.invest.extensions.InvestUiExtension;

@Configuration
public class ExampleHtmlUi implements InvestUiExtension {

  @Override
  public String getId() {
    return "example-html";
  }
}
