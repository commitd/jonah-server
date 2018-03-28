package io.committed.ketos.graphql;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec;

import reactor.core.publisher.Flux;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.server.graphql.data.GraphQlQuery;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;

public abstract class AbstractKetosGraphqlTest {

  @Autowired private WebTestClient client;

  private BaleenCorpus testCorpus;

  public AbstractKetosGraphqlTest() {
    this.testCorpus = new BaleenCorpus(GraphqlTestConfiguration.TEST_DATASET, "testCorpus", "");
  }

  public BaleenCorpus getTestCorpus() {
    return testCorpus;
  }

  protected Map<String, Object> defaultVariables() {
    return Collections.singletonMap("corpus", GraphqlTestConfiguration.TEST_DATASET);
  }

  protected BodyContentSpec postQuery(String queryText, Map<String, Object> variables) {
    GraphQlQuery query = new GraphQlQuery();
    query.setQuery(queryText);
    query.setVariables(variables);
    return client
        .post()
        .uri("/graphql")
        .syncBody(query)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody();
  }

  protected String corpusQuery(String body) {
    return "query($corpus: String!) { corpus(id: $corpus) { " + body + " } }";
  }

  protected BaleenRelation createRelation(String id, String sourceID, String targetId) {
    BaleenMention source = new BaleenMention();
    BaleenMention target = new BaleenMention();
    source.setId(sourceID);
    target.setId(targetId);
    return createRelation(id, source, target);
  }

  protected BaleenRelation createRelation(String id, BaleenMention source, BaleenMention target) {
    return new BaleenRelation(id, "doc", 0, 0, "Test", "TestSub", "", source, target, null);
  }

  protected BaleenDocument getTestDoc() {
    return new BaleenDocument("testDoc", null, "", null);
  }

  protected Flux<TermBin> getTestTermBins() {
    TermBin bin = new TermBin();
    bin.setTerm("test");
    bin.setCount(1);
    return Flux.fromIterable(Collections.singletonList(bin));
  }
}
