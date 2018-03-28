package io.committed.ketos.graphql.baleen.documentsearch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusDocumentsService;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentSearchDocumentsServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public CorpusDocumentsService corpusDocumentService(DataProviders providers) {
      return new CorpusDocumentsService(providers);
    }

    @Bean
    public DocumentSearchDocumentsService documentSearchDocumentsService(
        DataProviders providers, CorpusDocumentsService documentsService) {
      return new DocumentSearchDocumentsService(providers, documentsService);
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

  @Autowired private DocumentProvider documentProvider;

  @Test
  public void testGetDocumentTypes() {
    when(documentProvider.countByField(any(), anyList(), anyInt())).thenReturn(getTestTermBins());
    postQuery(
            corpusQuery(
                "searchDocuments(query: {id: \"testDoc\"}) { countByField(field: \"test\") { bins { term count } } }"),
            defaultVariables())
        .jsonPath("$.data.corpus.searchDocuments.countByField.bins")
        .isArray()
        .jsonPath("$.data.corpus.searchDocuments.countByField.bins[0].term")
        .isEqualTo("test")
        .jsonPath("$.data.corpus.searchDocuments.countByField.bins[0].count")
        .isEqualTo(1);
  }
}
