package io.committed.ketos.graphql;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec;

import io.committed.invest.server.graphql.data.GraphQlQuery;
import io.committed.ketos.common.data.BaleenCorpus;

public abstract class AbstractKetosGraphqlTest {

  @Autowired
  private WebTestClient client;

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
    return client.post()
        .uri("/graphql")
        .syncBody(query)
        .exchange()
        .expectStatus().isOk()
        .expectBody();
  }

  protected String corpusQuery(String body) {
    return "query($corpus: String!) { corpus(id: $corpus) { " + body + " } }";
  }



}
