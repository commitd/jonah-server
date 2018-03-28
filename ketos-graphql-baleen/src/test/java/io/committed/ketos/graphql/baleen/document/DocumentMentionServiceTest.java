package io.committed.ketos.graphql.baleen.document;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusDocumentsService;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentMentionServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public MentionProvider mentionProvider() {
      MentionProvider mock = Mockito.mock(MentionProvider.class);
      when(mock.getDataset()).thenReturn(GraphqlTestConfiguration.TEST_DATASET);
      when(mock.getDatabase()).thenReturn(GraphqlTestConfiguration.TEST_DB);
      when(mock.getDatasource()).thenReturn(GraphqlTestConfiguration.TEST_DATASOURCE);
      return mock;
    }

    @Bean
    public DocumentProvider documentProvider() {
      DocumentProvider mock = Mockito.mock(DocumentProvider.class);
      BaleenDocument doc = new BaleenDocument("testDoc", null, "", null);
      when(mock.getById(anyString())).thenReturn(Mono.just(doc));
      when(mock.getDataset()).thenReturn(GraphqlTestConfiguration.TEST_DATASET);
      when(mock.getDatabase()).thenReturn(GraphqlTestConfiguration.TEST_DB);
      when(mock.getDatasource()).thenReturn(GraphqlTestConfiguration.TEST_DATASOURCE);
      return mock;
    }

    @Bean
    public CorpusDocumentsService corpusDocumentService(DataProviders providers) {
      return new CorpusDocumentsService(providers);
    }

    @Bean
    public DocumentMentionService documentMentionService(DataProviders providers) {
      return new DocumentMentionService(providers);
    }
  }

  @Autowired public MentionProvider mentionProvider;

  @Test
  public void testGetMentionsByDocument() {

    List<BaleenMention> mentions =
        Collections.singletonList(
            new BaleenMention("mid", 0, 1, "test", "testSub", "testVal", "1", "2", null));
    when(mentionProvider.getByDocument(any(BaleenDocument.class)))
        .thenReturn(Flux.fromIterable(mentions));

    postQuery(
            corpusQuery("document(id: \"testDoc\") { mentions { id begin end type value } }"),
            defaultVariables())
        .jsonPath("$.data.corpus.document.mentions")
        .isArray()
        .jsonPath("$.data.corpus.document.mentions[0].id")
        .isEqualTo("mid")
        .jsonPath("$.data.corpus.document.mentions[0].begin")
        .isEqualTo(0)
        .jsonPath("$.data.corpus.document.mentions[0].end")
        .isEqualTo(1)
        .jsonPath("$.data.corpus.document.mentions[0].type")
        .isEqualTo("test")
        .jsonPath("$.data.corpus.document.mentions[0].value")
        .isEqualTo("testVal");
  }
}
