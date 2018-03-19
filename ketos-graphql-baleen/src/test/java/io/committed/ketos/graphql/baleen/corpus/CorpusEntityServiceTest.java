package io.committed.ketos.graphql.baleen.corpus;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import reactor.core.publisher.Mono;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CorpusEntityServiceTest extends AbstractKetosGraphqlTest {

  @Autowired
  private CorpusEntityService corpusEntityService;

  @Autowired
  private EntityProvider entityProvider;


  @TestConfiguration
  static class TestContextConfig {
    @Bean
    public EntityProvider entityProvider() {
      EntityProvider mock = Mockito.mock(EntityProvider.class);
      when(mock.getDataset()).thenReturn(GraphqlTestConfiguration.TEST_DATASET);
      when(mock.getDatabase()).thenReturn(GraphqlTestConfiguration.TEST_DB);
      when(mock.getDatasource()).thenReturn(GraphqlTestConfiguration.TEST_DATASOURCE);
      return mock;
    }

    @Bean
    public CorpusEntityService service(DataProviders providers) {
      return new CorpusEntityService(providers);
    }
  }

  @After
  public void teardown() {
    Mockito.validateMockitoUsage();
  }

  @Test
  public void testGetById() {
    when(entityProvider.getById(eq("test")))
        .thenReturn(Mono.just(new BaleenEntity("test", "test", "test", "test", "test", null)));
    postQuery("query($corpus: String!) { corpus(id: $corpus) { entity(id: \"test\" { id } }", defaultVariables())
        .jsonPath("$.errors").exists()
        .jsonPath("$.errors").isArray();
  }

}
