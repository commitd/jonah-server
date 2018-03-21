package io.committed.ketos.graphql.baleen.entity;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusDocumentsService;
import io.committed.ketos.graphql.baleen.corpus.CorpusEntityService;
import reactor.core.publisher.Mono;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class EntityDocumentsServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public CorpusDocumentsService corpusDocumentService(DataProviders providers) {
      return new CorpusDocumentsService(providers);
    }

    @Bean
    public CorpusEntityService CorpusEntityService(DataProviders providers) {
      return new CorpusEntityService(providers);
    }

    @Bean
    public EntityDocumentsService entityDocumentsService(DataProviders dataProviders,
        CorpusDocumentsService documentsService) {
      return new EntityDocumentsService(dataProviders, documentsService);
    }

    @Bean
    public DocumentProvider dataProvider() {
      return GraphqlTestConfiguration.getMockedProvider(DocumentProvider.class);
    }

    @Bean
    public EntityProvider entityProvider() {
      return GraphqlTestConfiguration.getMockedProvider(EntityProvider.class);
    }
  }

  @Autowired
  public DocumentProvider documentProvider;

  @Autowired
  public EntityProvider entityProvider;

  @Test
  public void testGetDocumentForEntity() {
    when(documentProvider.getById(eq("testDoc"))).thenReturn(Mono.just(getTestDoc()));
    BaleenEntity entity = new BaleenEntity("testEnt", "testDoc", "test", "", "testing", null);
    when(entityProvider.getById(anyString())).thenReturn(Mono.just(entity));
    postQuery(corpusQuery("entity(id: \"1\"){ document { id } }"), defaultVariables())
        .jsonPath("$.data.corpus.entity.document.id").isEqualTo("testDoc");
  }

}
