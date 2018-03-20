package io.committed.ketos.graphql.baleen.corpus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import reactor.core.publisher.Flux;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CorpusMetadataServiceTest extends AbstractKetosGraphqlTest {

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

    @Bean
    public CorpusMetadataService service(DataProviders providers) {
      return new CorpusMetadataService(providers);
    }
  }

  @Autowired
  private MetadataProvider metadataProvider;

  @Test
  public void testGetMetadata() {
    postQuery(corpusQuery("metadata(key: \"test\"){key}"), defaultVariables())
        .jsonPath("$.data.corpus.metadata.key").isEqualTo("test");
  }

  @Test
  public void testGetMetadataKey() {
    List<TermBin> results = Collections.singletonList(new TermBin("test", 1));
    when(metadataProvider.countByKey(any(), anyInt())).thenReturn(Flux.fromIterable(results));
    postQuery(corpusQuery("metadata { keys { bins { term count } } }"), defaultVariables())
        .jsonPath("$.data.corpus.metadata.keys.bins").isArray()
        .jsonPath("$.data.corpus.metadata.keys.bins[0].term").isEqualTo("test")
        .jsonPath("$.data.corpus.metadata.keys.bins[0].count").isEqualTo(1);
  }

  @Test
  public void testGetValues() {
    List<TermBin> results = Collections.singletonList(new TermBin("test", 1));
    when(metadataProvider.countByValue(any(), anyInt())).thenReturn(Flux.fromIterable(results));
    postQuery(corpusQuery("metadata { values { bins { term count } } } "), defaultVariables())
        .jsonPath("$.data.corpus.metadata.values.bins").isArray()
        .jsonPath("$.data.corpus.metadata.values.bins[0].term").isEqualTo("test")
        .jsonPath("$.data.corpus.metadata.values.bins[0].count").isEqualTo(1);
  }
}
