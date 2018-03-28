package io.committed.ketos.core.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import io.committed.invest.extensions.data.dataset.Dataset;
import lombok.Data;

/**
 * Core Settings (for Yaml).
 *
 * Note you can list zero, one or more {@link MongoCorpus}, {@link ElasticsearchCorpus} to add more
 * output from Baleen.
 */
@ConfigurationProperties(prefix = "ketos.core")
@Data
public class KetosCoreSettings {

  private List<Dataset> datasets;

  private List<MongoCorpus> mongo;

  private List<ElasticsearchCorpus> elasticsearch;

  private MongoFeedback feedback;

}
