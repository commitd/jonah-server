package io.committed.ketos.ui.entitydetails;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import io.committed.invest.extensions.InvestUiExtension;

/**
 * Extension point.
 *
 */
// TODO: When you change the name of this class or package you must also change the
// src/main/resources/META-INF/spring.factories file to reflect the fully
// qualified name of this file.
@Configuration
@EnableConfigurationProperties(EntityDetailsSettings.class)
public class EntityDetailsExtension implements InvestUiExtension {

  @Override
  public String getId() {
    // This must match the plugin.id in Maven pom.xml
    return "EntityDetails";
  }

  @Override
  public String getName() {
    return "Entity Details";
  }

  @Override
  public String getDescription() {
    return "View details of an entity";
  }

  @Override
  public String getIcon() {
    return "user";
  }

  @Override
  public Class<?> getSettings() {
    return EntityDetailsSettings.class;
  }

}
