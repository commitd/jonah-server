package io.committed.ketos.ui.map;


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
@EnableConfigurationProperties(ProjectTemplateSettings.class)
public class ProjectTemplateExtension implements InvestUiExtension {

  @Override
  public String getId() {
    // This must match the plugin.id in Maven pom.xml
    return "ProjectTemplate";
  }

  @Override
  public String getName() {
    return "Project Template";
  }

  @Override
  public String getDescription() {
    return "Template layout for Ketos Projects";
  }

  @Override
  public String getIcon() {
    return "browser";
  }

  @Override
  public Class<?> getSettings() {
    return ProjectTemplateSettings.class;
  }

}
