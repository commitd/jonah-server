package io.committed.ketos.graphql.baleen.document;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.input.DocumentProbe;
import io.committed.ketos.common.graphql.input.EntityProbe;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusDocumentsService;
import io.committed.ketos.graphql.baleen.corpus.CorpusEntityService;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentEntityServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {
    @Bean
    public CorpusEntityService corpusEntityService() {
      CorpusEntityService service = Mockito.mock(CorpusEntityService.class);
      List<BaleenEntity> results =
          Collections.singletonList(new BaleenEntity("test", "testDoc", "test", "", "value", null));
      when(service.getEntities(
              any(BaleenCorpus.class), any(EntityProbe.class), anyInt(), anyInt(), isNull()))
          .thenReturn(Flux.fromIterable(results));

      return service;
    }

    @Bean
    public CorpusDocumentsService corpusDocumentService(DataProviders providers) {
      return new CorpusDocumentsService(providers);
    }

    @Bean
    public DocumentEntityService documentEntityService(
        DataProviders providers, CorpusEntityService entityService) {
      return new DocumentEntityService(providers, entityService);
    }

    @Bean
    public DocumentProvider dataProvider() {
      DocumentProvider mock = Mockito.mock(DocumentProvider.class);
      when(mock.getDataset()).thenReturn(GraphqlTestConfiguration.TEST_DATASET);
      when(mock.getDatabase()).thenReturn(GraphqlTestConfiguration.TEST_DB);
      when(mock.getDatasource()).thenReturn(GraphqlTestConfiguration.TEST_DATASOURCE);
      BaleenDocument doc = new BaleenDocument("testDoc", null, "", null);
      when(mock.getById(anyString())).thenReturn(Mono.just(doc));
      when(mock.getByExample(any(DocumentProbe.class), anyInt(), anyInt()))
          .thenReturn(Flux.fromIterable(Collections.singletonList(doc)));
      return mock;
    }
  }

  @Test
  public void testDocumentEntities() {
    postQuery(corpusQuery("document(id: \"testDoc\") { entities { id } }"), defaultVariables())
        .jsonPath("$.data.corpus.document.entities")
        .isArray()
        .jsonPath("$.data.corpus.document.entities[0].id")
        .isEqualTo("test");
  }

  @Test
  public void testDocumentEntitiesWithProbe() {
    postQuery(
            corpusQuery("document(id: \"testDoc\") { entities(probe:{id: \"testDoc\"}){ id } }"),
            defaultVariables())
        .jsonPath("$.data.corpus.document.entities")
        .isArray()
        .jsonPath("$.data.corpus.document.entities[0].id")
        .isEqualTo("test");
  }
}
