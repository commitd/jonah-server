package io.committed.ketos.graphql.baleen.corpus;

import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import io.committed.invest.extensions.data.dataset.Dataset;
import io.committed.ketos.common.providers.baleen.DocumentProvider;

@TestConfiguration
public class GraphqlTestConfiguration {

  public static final String TEST_DATASET = "testDataset";

  @Bean
  public DocumentProvider documentProvider() {
    DocumentProvider mock = Mockito.mock(DocumentProvider.class);
    when(mock.getDataset()).thenReturn(TEST_DATASET);
    when(mock.getDatabase()).thenReturn("testDB");
    when(mock.getDatasource()).thenReturn("testDatasource");
    return mock;
  }

  @Bean
  public Dataset testDataset() {
    return new Dataset(TEST_DATASET, "Test Dataset", "", new ArrayList<>());
  }

}
