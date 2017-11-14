package io.committed.ketos.core.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.committed.vessel.server.data.dataset.DataProviderSpecification;
import io.committed.vessel.server.data.dataset.Dataset;

@Configuration
@ConfigurationProperties(prefix = "ketos.core")
public class KetosCoreSettings {

  // TODO: We add this because currently setDataset is called only after
  private List<Dataset> datasets = Arrays.asList(
      Dataset.builder().id("re3d").name("RE3D").description("Dstl's RE3D")
          .providers(Arrays.asList(
              DataProviderSpecification.builder().factory("baleen-mongo-documents")
                  .datasource("re3d").build(),
              DataProviderSpecification.builder().factory("baleen-mongo-entities")
                  .datasource("re3d").build(),
              DataProviderSpecification.builder().factory("baleen-mongo-mentions")
                  .datasource("re3d").build(),
              DataProviderSpecification.builder().factory("baleen-mongo-relations")
                  .datasource("re3d").build()))
          .build());


  @Bean
  public List<Dataset> getDatasets() {
    return datasets;
  }

  public void setDatasets(final List<Dataset> datasets) {
    this.datasets = datasets;
  }

}
