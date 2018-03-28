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
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusDocumentsService;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentRelationServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public RelationProvider relationProvider() {
      RelationProvider mock = Mockito.mock(RelationProvider.class);
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
    public DocumentRelationService documentMentionService(DataProviders providers) {
      return new DocumentRelationService(providers);
    }
  }

  @Autowired public RelationProvider relationProvider;

  @Test
  public void testGetMentionsByDocument() {

    List<BaleenRelation> relations =
        Collections.singletonList(createRelation("rel", "source", "target"));
    when(relationProvider.getByDocument(any(BaleenDocument.class)))
        .thenReturn(Flux.fromIterable(relations));

    postQuery(
            corpusQuery(
                "document(id: \"testDoc\") { relations { id source { id } target { id } } }"),
            defaultVariables())
        .jsonPath("$.data.corpus.document.relations")
        .isArray()
        .jsonPath("$.data.corpus.document.relations[0].id")
        .isEqualTo("rel")
        .jsonPath("$.data.corpus.document.relations[0].source.id")
        .isEqualTo("source")
        .jsonPath("$.data.corpus.document.relations[0].target.id")
        .isEqualTo("target");
  }
}
