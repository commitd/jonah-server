package io.committed.ketos.core.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import io.committed.invest.server.data.dataset.Dataset;

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
