package io.committed.ketos.ui.metadataexplorer;


import java.util.Arrays;
import java.util.Collection;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import io.committed.ketos.common.ui.actions.KetosCoreActions;
import io.committed.vessel.actions.ActionDefinition;
import io.committed.vessel.actions.SimpleActionDefinition;
import io.committed.vessel.extensions.VesselUiExtension;

/**
 * Extension point for Metadata.
 *
 */
@Configuration
@EnableConfigurationProperties(MetadataExplorerSettings.class)
public class MetadataExplorerPlugin implements VesselUiExtension {

  @Override
  public String getId() {
    return "MetadataExplorer";
  }

  @Override
  public String getName() {
    return "Metadata";
  }

  @Override
  public String getDescription() {
    return "Explore metadata available in the corpus";
  }

  @Override
  public String getIcon() {
    return "browser";
  }

  @Override
  public Class<?> getSettings() {
    return MetadataExplorerSettings.class;
  }

  @Override
  public Collection<ActionDefinition> getActions() {
    return Arrays.asList(
        SimpleActionDefinition.builder().title("Metadata").description("Explore metadata")
            .action(KetosCoreActions.CORPUS_VIEW).build());
  }

  @Override
  public String getStaticResourcePath() {
    // Do not change this without also changing the pom.xml copy-resources
    // as Maven will copy the output from the JS build into this location
    return "/ui/MetadataExplorer/";
  }
}
