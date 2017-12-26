package io.committed.ketos.ui.documentreader;


import java.util.Arrays;
import java.util.Collection;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.committed.invest.actions.ActionDefinition;
import io.committed.invest.actions.SimpleActionDefinition;
import io.committed.invest.extensions.InvestUiExtension;
import io.committed.ketos.common.ui.actions.KetosCoreActions;

/**
 * Extension point for CorpusSummary.
 *
 */
@Configuration
@ComponentScan
public class DocumentReaderExtension implements InvestUiExtension {

  @Override
  public String getId() {
    return "DocumentReader";
  }

  @Override
  public String getName() {
    return "Document reader";
  }

  @Override
  public String getDescription() {
    return "Read documents content and view metadata";
  }

  @Override
  public String getIcon() {
    return "file text outline";
  }

  @Override
  public Collection<ActionDefinition> getActions() {
    return Arrays.asList(
        SimpleActionDefinition.builder()
            .action(KetosCoreActions.DOCUMENT_VIEW)
            .title("Read")
            .description(getDescription())
            .build());

  }

  @Override
  public Class<?> getSettings() {
    return DocumentReaderSettings.class;
  }

  @Override
  public String getStaticResourcePath() {
    // Do not change this without also changing the pom.xml copy-resources
    // as Maven will copy the output from the JS build into this location
    return "/ui/DocumentReader/";
  }
}
