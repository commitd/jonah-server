package io.committed.ketos.core.config;

import java.util.Collections;
import java.util.List;
import io.committed.invest.extensions.data.dataset.DataProviderSpecification;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Mongo feedback data definition.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MongoFeedback extends AbstractKetosDataDefinition {

  private String host = "localhost";

  private int port = 27017;

  private String db = "ketos_feedback";

  public MongoFeedback() {
    super("feedback", "Feedback", "User feedback");
  }

  @Override
  protected List<DataProviderSpecification> createProviders() {
    return Collections.singletonList(
        DataProviderSpecification.builder()
            .datasource(getDatasource())
            .factory("feedback-mongo")
            .settings(newSettings()
                .put("uri", MongoCorpus.toMongoUri(host, port))
                .put("db", db)
                .build())
            .build());
  }
}
