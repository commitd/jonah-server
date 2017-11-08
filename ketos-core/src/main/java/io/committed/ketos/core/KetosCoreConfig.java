package io.committed.ketos.core;

import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.committed.ketos.core.config.KetosCoreSettings;
import io.committed.vessel.server.data.dataset.Dataset;

@Configuration
@ComponentScan(basePackageClasses = { KetosCoreConfig.class })
@EnableConfigurationProperties(KetosCoreSettings.class)
public class KetosCoreConfig {

  @Bean
  public List<Dataset> datasets(final KetosCoreSettings settings) {
    return settings.getDatasets();
  }

}
