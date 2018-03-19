package io.committed.ketos.graphql.baleen.corpus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.server.data.services.DefaultDatasetProviders;
import io.committed.invest.server.data.services.DefaultDatasetRegistry;
import io.committed.invest.server.graphql.GraphQlConfig;
import io.committed.invest.server.graphql.data.GraphQlQuery;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentProbe;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.output.DocumentSearch;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.graphql.baleen.root.CorpusService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@WebFluxTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {JacksonAutoConfiguration.class,
        CorpusDocumentsService.class,
        GraphqlTestConfiguration.class,
        DefaultDatasetProviders.class,
        CorpusService.class,
        DefaultDatasetRegistry.class})
@Import({GraphQlConfig.class})
@DirtiesContext
public class CorpusDocumentsServiceTest {

  @Autowired
  private CorpusDocumentsService corpusDocumentsService;

  @Autowired
  private DocumentProvider documentProvider;

  @Autowired
  private WebTestClient webClient;

  private BaleenCorpus testCorpus;

  @Before
  public void setup() {
    testCorpus = new BaleenCorpus(GraphqlTestConfiguration.TEST_DATASET, "testCorpus", "");
  }

  @After
  public void tearDown() {
    Mockito.validateMockitoUsage();
  }

  @Test
  public void testDocument() {
    when(documentProvider.getById(anyString()))
        .thenReturn(Mono.just(new BaleenDocument("test", null, "", null)));
    postQuery("query($corpus: String!) { corpus(id: $corpus) { document(id: \"test\"){ id } } }",
        Collections.singletonMap("corpus", GraphqlTestConfiguration.TEST_DATASET))
            .jsonPath("$.errors").doesNotExist()
            .jsonPath("$.data.corpus.document.id").isEqualTo("test");

    StepVerifier.create(corpusDocumentsService.getDocument(testCorpus, "test", null))
        .assertNext(d -> assertNotNull(d.getParent()))
        .verifyComplete();
  }

  @Test
  public void testGetDocumentNullId() {
    postQuery("query(($corpus: String!) { corpus(id: $corpus) { document(id: $id){ id } } }",
        Collections.singletonMap("corpus", GraphqlTestConfiguration.TEST_DATASET))
            .jsonPath("$.errors").isNotEmpty()
            .jsonPath("$.data").doesNotExist();
  }

  @Test
  public void testNoDocument() {
    when(documentProvider.getById(anyString())).thenReturn(Mono.empty());
    postQuery("query($corpus: String!) { corpus(id: $corpus) { document(id: \"test\"){ id } } }",
        Collections.singletonMap("corpus", GraphqlTestConfiguration.TEST_DATASET))
            .jsonPath("$.errors").doesNotExist()
            .jsonPath("$.data.corpus").exists()
            .jsonPath("$.data.corpus.document").isEqualTo(null);
  }

  @Test
  public void testGetDocumentByExample() {
    List<BaleenDocument> docs = Collections.singletonList(new BaleenDocument("test", null, "", null));
    when(documentProvider.getAll(anyInt(), anyInt())).thenReturn(Flux.fromIterable(docs));
    when(documentProvider.getByExample(any(DocumentProbe.class), anyInt(), anyInt()))
        .thenReturn(Flux.fromIterable(docs));
    postQuery("query($corpus: String!) { corpus(id: $corpus) { documents{ id } } }",
        Collections.singletonMap("corpus", GraphqlTestConfiguration.TEST_DATASET))
            .jsonPath("$.errors").doesNotExist()
            .jsonPath("$.data.corpus.documents").isArray();

    StepVerifier.create(corpusDocumentsService.getDocumentByExample(testCorpus, null, 0, 10, null))
        .assertNext(d -> assertNotNull(d.getParent()))
        .verifyComplete();
  }

  @Test
  public void testSearchDocuments() {
    DocumentFilter filter = new DocumentFilter();
    filter.setId("docId");
    EntityFilter entityFilter = new EntityFilter();
    entityFilter.setId("entityId");
    RelationFilter relationFilter = new RelationFilter();
    relationFilter.setId("relationId");
    MentionFilter mentionFilter = new MentionFilter();
    mentionFilter.setId("mentionId");

    DocumentSearch documents =
        corpusDocumentsService.getDocuments(testCorpus, filter, Collections.singletonList(mentionFilter),
            Collections.singletonList(entityFilter), Collections.singletonList(relationFilter));

    assertEquals(testCorpus, documents.getParent());
    assertEquals(mentionFilter, documents.getMentionFilters().get(0));
    assertEquals(entityFilter, documents.getEntityFilters().get(0));
    assertEquals(relationFilter, documents.getRelationFilters().get(0));
  }

  @Test
  public void testSearchDocumentsNullArgs() {
    postQuery("query($corpus: String!) { corpus(id: $corpus) { searchDocuments { hits { total } } } }",
        Collections.singletonMap("corpus", GraphqlTestConfiguration.TEST_DATASET))
            .jsonPath("$.errors").isArray()
            .jsonPath("$.errors").isNotEmpty();
  }

  @Test
  public void testGetDocumentTypes() {
    List<TermBin> bins = Collections.singletonList(new TermBin("test", 1));
    when(documentProvider.countByField(any(), anyList(), anyInt()))
        .thenReturn(Flux.fromIterable(bins));
    postQuery(
        "query($corpus: String!) { corpus(id: $corpus) { countByDocumentField(field: \"id\") { bins { term } } } }",
        Collections.singletonMap("corpus", GraphqlTestConfiguration.TEST_DATASET))
            .jsonPath("$.errors").doesNotExist()
            .jsonPath("$.data.corpus.countByDocumentField.bins").isArray()
            .jsonPath("$.data.corpus.countByDocumentField.bins[0].term").isEqualTo("test");
  }

  @Test
  public void testGetDocumentTypesNullArgs() {
    postQuery("query($corpus: String!) { corpus(id: $corpus) { countByDocumentField{ count } } }",
        Collections.singletonMap("corpus", GraphqlTestConfiguration.TEST_DATASET))
            .jsonPath("$.errors").isNotEmpty();
  }

  @Test
  public void testGetDocumentTypesEmptyArgs() {
    postQuery("query($corpus: String!) { corpus(id: $corpus) { countByDocumentField(field: \"\"){ count } } }",
        Collections.singletonMap("corpus", GraphqlTestConfiguration.TEST_DATASET))
            .jsonPath("$.errors").doesNotExist()
            .jsonPath("$.data.corpus.countByDocumentField").isEqualTo(null);
  }

  private BodyContentSpec postQuery(String queryText, Map<String, Object> variables) {
    GraphQlQuery query = new GraphQlQuery();
    query.setQuery(queryText);
    query.setVariables(variables);
    return webClient.post()
        .uri("/graphql")
        .syncBody(query)
        .exchange()
        .expectStatus().isOk()
        .expectBody();
  }



}
