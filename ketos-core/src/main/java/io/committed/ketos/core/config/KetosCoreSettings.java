package io.committed.ketos.core.config;

import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import io.committed.vessel.server.data.dataset.DataProviderSpecification;
import io.committed.vessel.server.data.dataset.Dataset;

@ConfigurationProperties(prefix = "ketos.core")
@Validated
public class KetosCoreSettings {

  @NotEmpty
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


  public void setDatasets(final List<Dataset> datasets) {
    this.datasets = datasets;
  }

  public List<Dataset> getDatasets() {
    return datasets;
  }

}
