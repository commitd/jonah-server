package io.committed.ketos.example.ui.html;

import org.springframework.context.annotation.Configuration;
import io.committed.invest.extensions.InvestUiExtension;

/**
 * An example of how you can use plain index.html in the src/resource/ui folder to create a Invest
 * UI plugin.
 *
 * This helps you package a UI plugin into a JAR / Java dependency to bundle into your runner, but
 * in reality a UI hosted on file system approach is preferred.
 */
@Configuration
public class ExampleHtmlUi implements InvestUiExtension {

  @Override
  public String getId() {
    return "example-html";
  }
}
