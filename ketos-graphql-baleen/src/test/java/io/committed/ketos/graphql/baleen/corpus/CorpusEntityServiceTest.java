package io.committed.ketos.graphql.baleen.corpus;


import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.input.EntityProbe;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CorpusEntityServiceTest extends AbstractKetosGraphqlTest {

  @Autowired
  private CorpusEntityService corpusEntityService;

  @Autowired
  private EntityProvider entityProvider;


  @TestConfiguration
  static class TestContextConfig {
    @Bean
    public EntityProvider entityProvider() {
      EntityProvider mock = Mockito.mock(EntityProvider.class);
      when(mock.getDataset()).thenReturn(GraphqlTestConfiguration.TEST_DATASET);
      when(mock.getDatabase()).thenReturn(GraphqlTestConfiguration.TEST_DB);
      when(mock.getDatasource()).thenReturn(GraphqlTestConfiguration.TEST_DATASOURCE);
      return mock;
    }

    @Bean
    public CorpusEntityService service(DataProviders providers) {
      return new CorpusEntityService(providers);
    }
  }

  @After
  public void teardown() {
    Mockito.validateMockitoUsage();
  }

  @Test
  public void testGetById() {
    when(entityProvider.getById(anyString()))
        .thenReturn(Mono.just(new BaleenEntity("test", "test", "test", "test", "test", null)));
    postQuery("query($corpus: String!) { corpus(id: $corpus) { entity(id: \"test\") { id } } }", defaultVariables())
        .jsonPath("$.data.corpus.entity.id").isEqualTo("test");

    StepVerifier.create(corpusEntityService.getById(getTestCorpus(), "test", null))
        .assertNext(e -> assertNotNull(e.getParent())).verifyComplete();
  }

  @Test
  public void testGetEntities() {
    BaleenEntity entity = createEntity("1", "doc", "Test");
    when(entityProvider.getAll(anyInt(), anyInt())).thenReturn(Flux.fromIterable(Collections.singletonList(entity)));
    postQuery("query($corpus: String!) { corpus(id: $corpus) { entities{ id } } }", defaultVariables())
        .jsonPath("$.data.corpus.entities").isArray()
        .jsonPath("$.data.corpus.entities[0].id").isEqualTo("1");
  }

  @Test
  public void testGetEntitiesWithProbe() {
    BaleenEntity entity = createEntity("1", "doc", "Test");
    when(entityProvider.getByExample(any(EntityProbe.class), anyInt(), anyInt()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(entity)));
    postQuery("query($corpus: String!) { corpus(id: $corpus) { entities(probe: {type: \"Person\"}){ id } } }",
        defaultVariables())
            .jsonPath("$.data.corpus.entities").isArray()
            .jsonPath("$.data.corpus.entities[0].id").isEqualTo("1");
  }

  @Test
  public void testSearchForEntities() {
    postQuery(
        "query($corpus: String!) { corpus(id: $corpus) { searchEntities(query: {type: \"Person\"}){  entityFilter { type } } } }",
        defaultVariables())
            .jsonPath("$.data.corpus.searchEntities.entityFilter.type").isEqualTo("Person");
  }

  @Test
  public void testSearchForEntitiesNullArgs() {
    postQuery(
        "query($corpus: String!) { corpus(id: $corpus) { searchEntities{  entityFilter { type } } } }",
        defaultVariables())
            .jsonPath("$.errors").exists()
            .jsonPath("$.errors").isArray();
  }

  @Test
  public void testCountEntities() {
    when(entityProvider.count()).thenReturn(Mono.just(10l));
    postQuery(
        "query($corpus: String!) { corpus(id: $corpus) { countEntities } }",
        defaultVariables())
            .jsonPath("$.data.corpus.countEntities").isEqualTo(10);
  }

  @Test
  public void testCountByField() {
    List<TermBin> results = Collections.singletonList(new TermBin("Person", 1));
    when(entityProvider.countByField(any(), anyList(), anyInt())).thenReturn(Flux.fromIterable(results));
    postQuery(
        "query($corpus: String!) { corpus(id: $corpus) { countByEntityField(field: \"type\"){ bins { term count } } } }",
        defaultVariables())
            .jsonPath("$.data.corpus.countByEntityField.bins").isArray()
            .jsonPath("$.data.corpus.countByEntityField.bins[0].term").isEqualTo("Person")
            .jsonPath("$.data.corpus.countByEntityField.bins[0].count").isEqualTo(1);
  }

  @Test
  public void testCountByFieldEmptyField() {
    postQuery(
        "query($corpus: String!) { corpus(id: $corpus) { countByEntityField(field: \"\"){ bins { term count } } } }",
        defaultVariables())
            .jsonPath("$.data.corpus.countByEntityField").isEqualTo(null)
            .jsonPath("$.data.corpus").exists();
  }

  @Test
  public void testCountByFieldNullField() {
    postQuery(
        "query($corpus: String!) { corpus(id: $corpus) { countByEntityField{ bins { term count } } } }",
        defaultVariables())
            .jsonPath("$.errors").exists();
  }

  private BaleenEntity createEntity(String id, String docId, String type) {
    return new BaleenEntity(id, docId, type, "", "", null);
  }

}
