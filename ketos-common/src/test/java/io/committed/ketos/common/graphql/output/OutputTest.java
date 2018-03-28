package io.committed.ketos.common.graphql.output;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class OutputTest {

  @Test
  public void testDocumentSearch() {
    final DocumentSearch documentSearch = new DocumentSearch(null, null, null, null, null);

    assertThat(documentSearch.hasEntities()).isFalse();
    assertThat(documentSearch.hasMentions()).isFalse();
    assertThat(documentSearch.hasParent()).isFalse();
    assertThat(documentSearch.hasRelations()).isFalse();

    assertThat(documentSearch.getEntityFilters()).isEmpty();
    assertThat(documentSearch.getMentionFilters()).isEmpty();
    assertThat(documentSearch.getRelationFilters()).isEmpty();
  }

  @Test
  public void testDocuments() {
    final Documents doc = new Documents(null, null, null, 0, 10);

    assertThat(doc.getResults().collectList().block()).isEmpty();
    assertThat(doc.getTotal().blockOptional()).isEmpty();
  }
}
