package io.committed.ketos.test.common.providers.baleen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.output.RelationSearch;
import io.committed.ketos.common.providers.baleen.RelationProvider;

public abstract class AbstractRelationProviderTest {

  public abstract RelationProvider getRelationProvider();

  @Test
  public void testGetAll() {
    List<BaleenRelation> all = getRelationProvider().getAll(0, 1000).collectList().block();
    assertEquals(1, all.size());
  }

  @Test
  public void testGetByDocument() {
    BaleenDocument doc = new BaleenDocument("a19f6ed4-87bb-4dc6-919e-596761127082", null, "", null);
    List<BaleenRelation> relations = getRelationProvider().getByDocument(doc).collectList().block();
    assertEquals(1, relations.size());
  }

  @Test
  public void testGetSourceRelations() {
    BaleenMention mention = new BaleenMention();
    mention.setId("b857d949-3ae6-4942-bcf5-e1e727eda58d");
    List<BaleenRelation> sourceRelations =
        getRelationProvider().getSourceRelations(mention).collectList().block();
    assertEquals(1, sourceRelations.size());
  }

  @Test
  public void testGetTargetRelations() {
    BaleenMention mention = new BaleenMention();
    mention.setId("a8672e25-0f23-43cf-b735-fba8f2d48f3b");
    List<BaleenRelation> targetRelations =
        getRelationProvider().getTargetRelations(mention).collectList().block();
    assertEquals(1, targetRelations.size());
  }

  @Test
  public void testGetById() {
    BaleenRelation relation =
        getRelationProvider().getById("9dec8723-5ba5-4fb1-8718-6c12586aaa89").block();
    assertNotNull(relation);
    assertEquals("9dec8723-5ba5-4fb1-8718-6c12586aaa89", relation.getId());
  }

  @Test
  public void testCount() {
    long count = getRelationProvider().count().block();
    assertEquals(1, count);
  }

  @Test
  public void testCountByField() {
    List<TermBin> results =
        getRelationProvider()
            .countByField(Optional.empty(), Collections.singletonList("type"), 1000)
            .collectList()
            .block();
    assertEquals(1, results.size());
    assertEquals(1, results.get(0).getCount());
  }

  @Test
  public void testSearch() {
    RelationFilter filter = new RelationFilter();
    filter.setType("associated");
    RelationSearch search = new RelationSearch(null, filter);
    List<BaleenRelation> results =
        getRelationProvider().search(search, 0, 1000).getResults().collectList().block();
    assertEquals(1, results.size());
  }
}
