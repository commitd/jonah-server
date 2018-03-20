package io.committed.ketos.graphql.baleen.corpus;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CorpusProviderServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {
    @Bean
    public MetadataProvider metadataProvider() {
      MetadataProvider mock = Mockito.mock(MetadataProvider.class);
      when(mock.getDataset()).thenReturn(GraphqlTestConfiguration.TEST_DATASET);
      when(mock.getDatabase()).thenReturn(GraphqlTestConfiguration.TEST_DB);
      when(mock.getDatasource()).thenReturn(GraphqlTestConfiguration.TEST_DATASOURCE);
      return mock;
    }
  }

  @Test
  public void testProviders() {
    postQuery(corpusQuery("providers { dataset }"), defaultVariables())
        .jsonPath("$.data.corpus.providers").isArray()
        .jsonPath("$.data.corpus.providers[0].dataset").isEqualTo(GraphqlTestConfiguration.TEST_DATASET);
  }

}
