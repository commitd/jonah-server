package io.committed.ketos.graphql.baleen.documentsearch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import io.committed.ketos.common.graphql.output.DocumentSearch;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusDocumentsService;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentSearchServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public CorpusDocumentsService corpusDocumentService(DataProviders providers) {
      return new CorpusDocumentsService(providers);
    }

    @Bean
    public DocumentSearchService documentSearchService(DataProviders dataProviders) {
      return new DocumentSearchService(dataProviders);
    }

    @Bean
    public DocumentProvider dataProvider() {
      DocumentProvider mock = Mockito.mock(DocumentProvider.class);
      when(mock.getDataset()).thenReturn(GraphqlTestConfiguration.TEST_DATASET);
      when(mock.getDatabase()).thenReturn(GraphqlTestConfiguration.TEST_DB);
      when(mock.getDatasource()).thenReturn(GraphqlTestConfiguration.TEST_DATASOURCE);
      return mock;
    }
  }

  @Autowired public DocumentProvider documentProvider;

  @Test
  public void testGetDocuments() {
    DocumentSearchResult result = new DocumentSearchResult();
    result.setResults(Flux.fromIterable(Collections.singletonList(getTestDoc())));
    result.setTotal(Mono.just(1l));
    when(documentProvider.search(any(DocumentSearch.class), anyInt(), anyInt())).thenReturn(result);
    postQuery(
            corpusQuery("searchDocuments(query: {id: \"testDoc\"}) { hits { results { id } } }"),
            defaultVariables())
        .jsonPath("$.data.corpus.searchDocuments.hits")
        .exists()
        .jsonPath("$.data.corpus.searchDocuments.hits.results")
        .isArray()
        .jsonPath("$.data.corpus.searchDocuments.hits.results[0].id")
        .isEqualTo("testDoc");
  }
}
