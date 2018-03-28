package io.committed.ketos.graphql;

import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import io.committed.invest.extensions.data.dataset.Dataset;
import io.committed.invest.extensions.data.providers.DataProvider;

@TestConfiguration
public class GraphqlTestConfiguration {

  public static final String TEST_DATASET = "testDataset";

  public static final String TEST_DB = "testDB";

  public static final String TEST_DATASOURCE = "testDatasource";

  @Bean
  public Dataset testDataset() {
    return new Dataset(TEST_DATASET, "Test Dataset", "", new ArrayList<>());
  }

  public static <T extends DataProvider> T getMockedProvider(Class<T> clazz) {
    T mock = Mockito.mock(clazz);
    when(mock.getDataset()).thenReturn(GraphqlTestConfiguration.TEST_DATASET);
    when(mock.getDatabase()).thenReturn(GraphqlTestConfiguration.TEST_DB);
    when(mock.getDatasource()).thenReturn(GraphqlTestConfiguration.TEST_DATASOURCE);
    return mock;
  }
}
