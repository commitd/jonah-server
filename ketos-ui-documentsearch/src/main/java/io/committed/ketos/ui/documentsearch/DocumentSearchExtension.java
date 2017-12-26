package io.committed.ketos.ui.documentsearch;


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
public class DocumentSearchExtension implements InvestUiExtension {

  @Override
  public String getId() {
    return "DocumentSearch";
  }

  @Override
  public String getName() {
    return "Document search";
  }

  @Override
  public String getDescription() {
    return "Find documents by content";
  }

  @Override
  public String getIcon() {
    return "search";
  }

  @Override
  public Class<?> getSettings() {
    return DocumentSearchSettings.class;
  }

  @Override
  public Collection<ActionDefinition> getActions() {
    return Arrays.asList(
        SimpleActionDefinition.builder()
            .action(KetosCoreActions.DOCUMENT_SEARCH)
            .title("Search")
            .description("Search for documents by content")
            .build(),
        SimpleActionDefinition.builder()
            .action(KetosCoreActions.CORPUS_VIEW)
            .title("Search")
            .description("Explore corpus by search")
            .build());

  }

  @Override
  public String getStaticResourcePath() {
    // Do not change this without also changing the pom.xml copy-resources
    // as Maven will copy the output from the JS build into this location
    return "/ui/DocumentSearch/";
  }
}
