package io.committed.ketos.core.config;

import java.util.LinkedList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.committed.invest.extensions.data.dataset.Dataset;

@Configuration
public class ExportDatasetConfig {

  @Bean
  public List<Dataset> getDatasets(final KetosCoreSettings settings) {

    final List<Dataset> datasets = new LinkedList<>();

    if (settings.getDatasets() != null) {
      datasets.addAll(settings.getDatasets());
    }

    if (settings.getElasticsearch() != null) {
      settings.getElasticsearch().stream()
          .map(ElasticsearchCorpus::toDataset)
          .forEach(datasets::add);
    }

    if (settings.getMongo() != null) {
      settings.getMongo().stream()
          .map(c -> c.toDataset())
          .forEach(datasets::add);
    }

    if (settings.getFeedback() != null) {
      datasets.add(settings.getFeedback().toDataset());
    }

    return datasets;
  }
}
