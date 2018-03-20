package io.committed.ketos.graphql.baleen.corpus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.graphql.input.RelationProbe;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CorpusRelationServiceTest extends AbstractKetosGraphqlTest {

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
    public CorpusRelationService service(DataProviders providers) {
      return new CorpusRelationService(providers);
    }
  }

  @Autowired
  private RelationProvider relationProvider;

  @Test
  public void testGetById() {
    BaleenRelation relation = createRelation("1", "2", "3");
    when(relationProvider.getById(anyString())).thenReturn(Mono.just(relation));
    postQuery(corpusQuery("relation(id: \"1\"){ id }"), defaultVariables())
        .jsonPath("$.data.corpus.relation.id").isEqualTo("1");
  }

  @Test
  public void testGetByIdNullArgs() {
    BaleenRelation relation = createRelation("1", "2", "3");
    when(relationProvider.getById(anyString())).thenReturn(Mono.just(relation));
    postQuery(corpusQuery("relation{ id }"), defaultVariables())
        .jsonPath("$.errors").exists();
  }

  @Test
  public void testGetAllRelations() {
    BaleenRelation relation = createRelation("1", "2", "3");
    when(relationProvider.getAll(anyInt(), anyInt()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(relation)));
    postQuery(corpusQuery(" relations { id }"), defaultVariables())
        .jsonPath("$.data.corpus.relations").isArray()
        .jsonPath("$.data.corpus.relations[0].id").isEqualTo("1");
  }

  @Test
  public void testGetAllRelationsWithProbe() {
    BaleenRelation relation = createRelation("1", "2", "3");
    when(relationProvider.getByExample(any(RelationProbe.class), anyInt(), anyInt()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(relation)));
    postQuery(corpusQuery(" relations(probe: {type: \"Test\"}){ id }"), defaultVariables())
        .jsonPath("$.data.corpus.relations").isArray()
        .jsonPath("$.data.corpus.relations[0].id").isEqualTo("1");
  }

  @Test
  public void testSearchForRleations() {
    postQuery(corpusQuery(" searchRelations(query: {id: \"test\"}){ relationFilter { id } }"), defaultVariables())
        .jsonPath("$.data.corpus.searchRelations.relationFilter").exists()
        .jsonPath("$.data.corpus.searchRelations.relationFilter.id").isEqualTo("test");
  }

  @Test
  public void testCountRelations() {
    when(relationProvider.count()).thenReturn(Mono.just(10l));
    postQuery(corpusQuery(" countRelations "), defaultVariables())
        .jsonPath("$.data.corpus.countRelations").isEqualTo(10);
  }

}
