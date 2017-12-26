package io.committed.ketos.ui.corpussummary;

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
public class CorpusSummaryExtension implements InvestUiExtension {

  @Override
  public String getId() {
    return "CorpusSummary";
  }

  @Override
  public Class<?> getSettings() {
    return CorpusSummarySettings.class;
  }

  @Override
  public String getName() {
    return "Corpus summary";
  }

  @Override
  public String getIcon() {
    return "line chart";
  }

  @Override
  public String getDescription() {
    return "Overview of corpus content";
  }

  @Override
  public Collection<ActionDefinition> getActions() {
    return Arrays.asList(
        SimpleActionDefinition.builder().title("Summary").description("Corpus dashboard")
            .action(KetosCoreActions.CORPUS_VIEW).build());
  }

  // TODO: You should override to provide additional information such name, description and logo

  @Override
  public String getStaticResourcePath() {
    // Do not change this without also changing the pom.xml copy-resources
    // as Maven will copy the output from the JS build into this location
    return "/ui/CorpusSummary/";
  }
}
