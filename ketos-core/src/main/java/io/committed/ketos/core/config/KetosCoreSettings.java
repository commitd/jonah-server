package io.committed.ketos.core.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import io.committed.vessel.server.data.dataset.Dataset;

@Configuration
@ConfigurationProperties(prefix = "ketos.core")
public class KetosCoreSettings {

  private List<Dataset> datasets;

  public List<Dataset> getDatasets() {
    return datasets;
  }

  public void setDatasets(final List<Dataset> datasets) {
    this.datasets = datasets;
  }

}
