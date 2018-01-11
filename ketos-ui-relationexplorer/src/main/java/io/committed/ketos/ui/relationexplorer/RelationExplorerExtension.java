package io.committed.ketos.ui.relationexplorer;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import io.committed.invest.extensions.InvestUiExtension;

/**
 * Extension point.
 *
 */
@Configuration
@EnableConfigurationProperties(RelationExplorerSettings.class)
public class RelationExplorerExtension implements InvestUiExtension {

  @Override
  public String getId() {
    // This must match the plugin.id in Maven pom.xml
    return "RelationExplorer";
  }

  @Override
  public String getName() {
    return "Relation Explorer";
  }

  @Override
  public String getDescription() {
    return "Explore relations between entities";
  }

  @Override
  public String getIcon() {
    return "user";
  }

  @Override
  public Class<?> getSettings() {
    return RelationExplorerSettings.class;
  }

}
