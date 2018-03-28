package io.committed.ketos.graphql.baleen.relation;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import reactor.core.publisher.Mono;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusRelationService;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RelationDocumentServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public CorpusRelationService corpusMentionService(DataProviders dataProviders) {
      return new CorpusRelationService(dataProviders);
    }

    @Bean
    public RelationDocumentService entitySearchService(DataProviders dataProviders) {
      return new RelationDocumentService(dataProviders);
    }

    @Bean
    public RelationProvider mentionProvider() {
      return GraphqlTestConfiguration.getMockedProvider(RelationProvider.class);
    }

    @Bean
    public DocumentProvider documentProvider() {
      return GraphqlTestConfiguration.getMockedProvider(DocumentProvider.class);
    }
  }

  @Autowired private RelationProvider relationProvider;

  @Autowired private DocumentProvider documentProvider;

  @Test
  public void testGetDocument() {
    BaleenRelation relation = createRelation("rel", "source", "target");
    relation.setDocId("testDoc");
    when(relationProvider.getById(anyString())).thenReturn(Mono.just(relation));
    when(documentProvider.getById(eq("testDoc"))).thenReturn(Mono.just(getTestDoc()));
    postQuery(corpusQuery("relation(id: \"rel\") { document { id } }"), defaultVariables())
        .jsonPath("$.data.corpus.relation.document.id")
        .isEqualTo("testDoc");
  }
}
