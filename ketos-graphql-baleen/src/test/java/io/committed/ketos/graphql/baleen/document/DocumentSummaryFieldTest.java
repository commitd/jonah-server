package io.committed.ketos.graphql.baleen.document;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import reactor.core.publisher.Mono;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentMetadata;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusDocumentsService;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentSummaryFieldTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public CorpusDocumentsService corpusDocumentService(DataProviders providers) {
      return new CorpusDocumentsService(providers);
    }

    @Bean
    public DocumentSummaryField documentSummaryField() {
      return new DocumentSummaryField();
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
  public void testGetSummaryFromMetadata() {
    BaleenDocumentMetadata metadata = new BaleenDocumentMetadata("summary", "metadataSummary");
    BaleenDocument doc =
        new BaleenDocument("testDoc", Collections.singletonList(metadata), "", null);
    when(documentProvider.getById(anyString())).thenReturn(Mono.just(doc));

    postQuery(corpusQuery("document(id: \"testDoc\") { summary }"), defaultVariables())
        .jsonPath("$.data.corpus.document.summary")
        .isEqualTo("metadataSummary");
  }

  @Test
  public void testGetSummaryFromContent() {
    BaleenDocument doc = new BaleenDocument("testDoc", null, "content", null);
    when(documentProvider.getById(anyString())).thenReturn(Mono.just(doc));

    postQuery(corpusQuery("document(id: \"testDoc\") { summary }"), defaultVariables())
        .jsonPath("$.data.corpus.document.summary")
        .isEqualTo("content");
  }

  @Test
  public void testGetSummaryFromKeywords() {
    BaleenDocumentMetadata metadata = new BaleenDocumentMetadata("keywords", "keywords");
    BaleenDocument doc =
        new BaleenDocument("testDoc", Collections.singletonList(metadata), "content", null);
    when(documentProvider.getById(anyString())).thenReturn(Mono.just(doc));

    postQuery(corpusQuery("document(id: \"testDoc\") { summary }"), defaultVariables())
        .jsonPath("$.data.corpus.document.summary")
        .isEqualTo("keywords");
  }

  @Test
  public void testGetSummaryFromMultipleKeywords() {
    List<BaleenDocumentMetadata> metadata = new ArrayList<>();
    metadata.add(new BaleenDocumentMetadata("keywords", "keywords"));
    metadata.add(new BaleenDocumentMetadata("keywords", "keywords2"));
    BaleenDocument doc = new BaleenDocument("testDoc", metadata, "content", null);
    when(documentProvider.getById(anyString())).thenReturn(Mono.just(doc));

    postQuery(corpusQuery("document(id: \"testDoc\") { summary }"), defaultVariables())
        .jsonPath("$.data.corpus.document.summary")
        .isEqualTo("keywords; keywords2");
  }

  @Test
  public void testNoSummary() {
    BaleenDocument doc = new BaleenDocument("testDoc", null, null, null);
    when(documentProvider.getById(anyString())).thenReturn(Mono.just(doc));

    postQuery(corpusQuery("document(id: \"testDoc\") { summary }"), defaultVariables())
        .jsonPath("$.data.corpus.document")
        .exists()
        .jsonPath("$.data.corpus.document.summary")
        .isEqualTo("");
  }
}
