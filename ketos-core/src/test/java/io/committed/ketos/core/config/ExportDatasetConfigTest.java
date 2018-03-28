package io.committed.ketos.core.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import io.committed.invest.extensions.data.dataset.Dataset;

public class ExportDatasetConfigTest {

  @Test
  public void testNoDatasets() {
    final KetosCoreSettings settings = new KetosCoreSettings();

    final ExportDatasetConfig config = new ExportDatasetConfig();
    final List<Dataset> datasets = config.getDatasets(settings);

    assertThat(datasets).isEmpty();
  }

  @Test
  public void testWithDatasets() {
    final KetosCoreSettings settings = new KetosCoreSettings();

    final List<Dataset> input = Arrays.asList(mock(Dataset.class), mock(Dataset.class));

    settings.setDatasets(input);

    final ExportDatasetConfig config = new ExportDatasetConfig();
    final List<Dataset> datasets = config.getDatasets(settings);

    assertThat(datasets).containsExactlyElementsOf(input);
  }

  @Test
  public void testWithMongoAndElastic() {
    final KetosCoreSettings settings = new KetosCoreSettings();

    final MongoCorpus edittableMongo = new MongoCorpus();
    edittableMongo.setEdittable(true);

    final ElasticsearchCorpus edittableEs = new ElasticsearchCorpus();
    edittableEs.setEdittable(true);
    settings.setMongo(Arrays.asList(new MongoCorpus(), edittableMongo));
    settings.setElasticsearch(
        Arrays.asList(new ElasticsearchCorpus(), new ElasticsearchCorpus(), edittableEs));

    settings.setFeedback(new MongoFeedback());

    final ExportDatasetConfig config = new ExportDatasetConfig();
    final List<Dataset> datasets = config.getDatasets(settings);

    // 2*mongo + 3*es + 1*feedback
    // TODO: could check we have exactly what we want here
    assertThat(datasets).hasSize(6);
  }
}
