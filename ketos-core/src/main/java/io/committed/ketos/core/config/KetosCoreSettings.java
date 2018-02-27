package io.committed.ketos.core.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import io.committed.invest.extensions.data.dataset.Dataset;
import lombok.Data;

@ConfigurationProperties(prefix = "ketos.core")
@Data
public class KetosCoreSettings {

  private List<Dataset> datasets;

  private List<MongoCorpus> mongo;

  private List<ElasticsearchCorpus> elasticsearch;

  private MongoFeedback feedback;


}
