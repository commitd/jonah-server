package io.committed.ketos.graphql;

import java.util.ArrayList;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import io.committed.invest.extensions.data.dataset.Dataset;

@TestConfiguration
public class GraphqlTestConfiguration {

  public static final String TEST_DATASET = "testDataset";

  public static final String TEST_DB = "testDB";

  public static final String TEST_DATASOURCE = "testDatasource";

  @Bean
  public Dataset testDataset() {
    return new Dataset(TEST_DATASET, "Test Dataset", "", new ArrayList<>());
  }

}
