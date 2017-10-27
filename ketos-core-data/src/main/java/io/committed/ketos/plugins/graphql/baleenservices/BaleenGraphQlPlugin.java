package io.committed.ketos.plugins.graphql.baleenservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.committed.ketos.plugins.graphql.baleenservices.providers.DataProvider;
import io.committed.ketos.plugins.providers.services.CorpusProviders;
import io.committed.vessel.extensions.VesselGraphQlExtension;

public class BaleenGraphQlPlugin implements VesselGraphQlExtension {

  @Override
  public Class<?> getConfiguration() {
    return PluginConfiguration.class;
  }


  @Configuration
  @ComponentScan(basePackageClasses = PluginConfiguration.class)
  public static class PluginConfiguration {


    @Bean
    public CorpusProviders corpusProviders(
        @Autowired(required = false) final List<DataProvider> providers) {
      final Map<String, List<DataProvider>> map = new HashMap<>();

      if (providers != null && !providers.isEmpty()) {
        // TODO: Hack until we have proper corpus provider implementation
        map.put("baleen", providers);
      }

      return new CorpusProviders(map);
    }
  }
}
