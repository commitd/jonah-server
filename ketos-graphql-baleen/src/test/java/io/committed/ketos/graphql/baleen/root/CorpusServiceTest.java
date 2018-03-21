package io.committed.ketos.graphql.baleen.root;

import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CorpusServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public DocumentProvider relationProvider() {
      DocumentProvider mockedProvider = GraphqlTestConfiguration.getMockedProvider(DocumentProvider.class);
      when(mockedProvider.getProviderType()).thenReturn("DocumentProvider");
      return mockedProvider;
    }
  }

  @Test
  public void testCorpus() {
    postQuery("query { corpus(id: \"" + GraphqlTestConfiguration.TEST_DATASET + "\") { id } }", Collections.EMPTY_MAP)
        .jsonPath("$.data.corpus.id").isEqualTo(getTestCorpus().getId());
  }

  @Test
  public void testCorpora() {
    postQuery("query { corpora { id } }", Collections.EMPTY_MAP)
        .jsonPath("$.data.corpora").isArray()
        .jsonPath("$.data.corpora[0].id").isEqualTo(getTestCorpus().getId());
  }

  @Test
  public void testCorporaWithDatabase() {
    postQuery("query { corpora(database: \"" + GraphqlTestConfiguration.TEST_DB + "\") { id } }", Collections.EMPTY_MAP)
        .jsonPath("$.data.corpora").isArray()
        .jsonPath("$.data.corpora[0].id").isEqualTo(getTestCorpus().getId());
  }

  @Test
  public void testCorporaWithDatasource() {
    postQuery("query { corpora(datasource: \"" + GraphqlTestConfiguration.TEST_DATASOURCE + "\") { id } }",
        Collections.EMPTY_MAP)
            .jsonPath("$.data.corpora").isArray()
            .jsonPath("$.data.corpora[0].id").isEqualTo(getTestCorpus().getId());
  }

  @Test
  public void testCorporaWithProvider() {
    postQuery("query { corpora(provider: \"DocumentProvider\") { id } }",
        Collections.EMPTY_MAP)
            .jsonPath("$.data.corpora").isArray()
            .jsonPath("$.data.corpora[0].id").isEqualTo(getTestCorpus().getId());
  }

  @Test
  public void testCorporaWithAllArgs() {
    postQuery("query { corpora(datasource: \"" + GraphqlTestConfiguration.TEST_DATASOURCE + "\", database: \""
        + GraphqlTestConfiguration.TEST_DB + "\", provider: \"DocumentProvider\") { id } }",
        Collections.EMPTY_MAP)
            .jsonPath("$.data.corpora").isArray()
            .jsonPath("$.data.corpora[0].id").isEqualTo(getTestCorpus().getId());
  }

}
