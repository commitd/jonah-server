package io.committed.ketos.graphql.baleen.relationsearch;

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

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.graphql.intermediate.RelationSearchResult;
import io.committed.ketos.common.graphql.output.RelationSearch;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusRelationService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RelationSearchServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public CorpusRelationService corpusRelationService(DataProviders dataProviders) {
      return new CorpusRelationService(dataProviders);
    }

    @Bean
    public RelationSearchService relationSearchService(DataProviders dataProviders) {
      return new RelationSearchService(dataProviders);
    }

    @Bean
    public RelationProvider relationProvider() {
      return GraphqlTestConfiguration.getMockedProvider(RelationProvider.class);
    }
  }

  @Autowired
  private RelationProvider relationProvider;

  @Test
  public void testGetRelationHits() {
    RelationSearchResult result = new RelationSearchResult();
    BaleenRelation relation = createRelation("rel", "source", "target");
    result.setResults(Flux.fromIterable(Collections.singletonList(relation)));
    result.setSize(1);
    result.setOffset(0);
    result.setTotal(Mono.just(1l));
    when(relationProvider.search(any(RelationSearch.class), anyInt(), anyInt())).thenReturn(result);
    postQuery(corpusQuery("searchRelations(query: {id: \"rel\"}) { hits { results { id} } }"), defaultVariables())
        .jsonPath("$.data.corpus.searchRelations.hits.results").isArray()
        .jsonPath("$.data.corpus.searchRelations.hits.results[0].id").isEqualTo("rel");
  }

}
