package io.committed.ketos.graphql.baleen.entitysearch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.intermediate.EntitySearchResult;
import io.committed.ketos.common.graphql.output.EntitySearch;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusEntityService;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class EntitySearchServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public CorpusEntityService CorpusEntityService(DataProviders providers) {
      return new CorpusEntityService(providers);
    }

    @Bean
    public EntitySearchService entitySearchService(DataProviders dataProviders) {
      return new EntitySearchService(dataProviders);
    }

    @Bean
    public EntityProvider entityProvider() {
      return GraphqlTestConfiguration.getMockedProvider(EntityProvider.class);
    }
  }

  @Autowired private EntityProvider entityProvider;

  @Test
  public void testGetHits() {
    BaleenEntity entity = new BaleenEntity("id", "docId", "type", "sub", "value", null);
    EntitySearchResult result =
        new EntitySearchResult(
            Flux.fromIterable(Collections.singletonList(entity)), Mono.just(1l), 0, 1);
    when(entityProvider.search(any(EntitySearch.class), anyInt(), anyInt())).thenReturn(result);
    postQuery(
            corpusQuery("searchEntities(query: {id: \"eid\"}){ hits { results { id } } }"),
            defaultVariables())
        .jsonPath("$.data.corpus.searchEntities.hits.results")
        .isArray()
        .jsonPath("$.data.corpus.searchEntities.hits.results[0].id")
        .isEqualTo("id");
  }
}
