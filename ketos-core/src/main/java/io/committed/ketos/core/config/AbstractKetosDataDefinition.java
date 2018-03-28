package io.committed.ketos.core.config;

import java.util.List;
import java.util.Map;

import lombok.Data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import io.committed.invest.extensions.data.dataset.DataProviderSpecification;
import io.committed.invest.extensions.data.dataset.Dataset;

/** Base for Ketos data definitions. */
@Data
public abstract class AbstractKetosDataDefinition {

  private String id;
  private String name;
  private String description;
  private String datasource;

  protected AbstractKetosDataDefinition(
      final String id, final String name, final String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public Dataset toDataset() {
    return Dataset.builder()
        .id(id)
        .description(description)
        .name(name)
        .providers(createProviders())
        .build();
  }

  public String getDatasource() {
    return datasource == null ? id : datasource;
  }

  protected abstract List<DataProviderSpecification> createProviders();

  protected ImmutableMap.Builder<String, Object> newSettings() {
    return ImmutableMap.builder();
  }

  protected Builder<String, Object> newSettings(final ImmutableMap<String, Object> baseSettings) {
    return ImmutableMap.<String, Object>builder().putAll(baseSettings);
  }

  protected Map<String, Object> merge(
      final Map<String, Object> generalSettings, final Map<String, Object> specificSettings) {
    final Builder<String, Object> settings = newSettings();

    if (generalSettings != null) {
      settings.putAll(generalSettings);
    }

    if (specificSettings != null) {
      settings.putAll(specificSettings);
    }

    return settings.build();
  }

  protected void addProvider(
      final List<DataProviderSpecification> providers,
      final String factory,
      final Map<String, Object> settings) {

    providers.add(
        DataProviderSpecification.builder()
            .datasource(getDatasource())
            .factory(factory)
            .settings(settings)
            .build());
  }
}
