package io.committed.ketos.example.ui.html;

import org.springframework.context.annotation.Configuration;

import io.committed.vessel.extensions.VesselUiExtension;

@Configuration
public class ExampleHtmlUi implements VesselUiExtension {

  @Override
  public String getId() {
    return "example-html";
  }
}
