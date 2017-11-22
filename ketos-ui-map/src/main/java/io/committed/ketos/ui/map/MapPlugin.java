package io.committed.ketos.ui.map;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import io.committed.vessel.extensions.VesselUiExtension;

/**
 * Extension point for Map.
 *
 */
@Configuration
@EnableConfigurationProperties(MapSettings.class)
public class MapPlugin implements VesselUiExtension {

  @Override
  public String getId() {
    return "Map";
  }

  @Override
  public String getName() {
    return "Map";
  }

  @Override
  public String getDescription() {
    return "Full size map";
  }

  @Override
  public String getIcon() {
    return "map";
  }

  @Override
  public Class<?> getSettings() {
    return MapSettings.class;
  }

  @Override
  public String getStaticResourcePath() {
    // Do not change this without also changing the pom.xml copy-resources
    // as Maven will copy the output from the JS build into this location
    return "/ui/Map/";
  }
}
