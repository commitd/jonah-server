package io.committed.ketos.core.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.committed.invest.extensions.data.dataset.Dataset;

@Configuration
public class ExportDatasetConfig {

  @Bean
  public List<Dataset> getDatasets(final KetosCoreSettings settings) {
    return settings.getDatasets();
  }
}
