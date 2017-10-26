package io.committed.ketos.plugins.graphql.baleenservices;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.committed.vessel.extensions.VesselGraphQlExtension;

public class BaleenGraphQlPlugin implements VesselGraphQlExtension {

  @Override
  public Class<?> getConfiguration() {
    return PluginConfiguration.class;
  }


  @Configuration
  @ComponentScan(basePackageClasses = PluginConfiguration.class)
  public static class PluginConfiguration {

  }
}
