package io.committed.ketos.test.common.providers.baleen;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.output.EntitySearch;
import io.committed.ketos.common.providers.baleen.EntityProvider;

public abstract class AbstractEntityProviderTest {

  public abstract EntityProvider getEntityProvider();

  @Test
  public void testGetById() {
    BaleenEntity entity = getEntityProvider().getById("42ad69a0-f11b-449e-947f-fe91f52be3d7").block();
    assertEquals("42ad69a0-f11b-449e-947f-fe91f52be3d7", entity.getId());
  }

  @Test
  public void testGetByDocument() {
    BaleenDocument doc = new BaleenDocument("a19f6ed4-87bb-4dc6-919e-596761127082", null, "", null);
    List<BaleenEntity> entities = getEntityProvider().getByDocument(doc).collectList().block();
    assertEquals(4, entities.size());
  }

  @Test
  public void testCountByField() {
    List<TermBin> results = getEntityProvider().countByField(Optional.empty(), Collections.singletonList("type"), 1000)
        .collectList().block();
    assertEquals(3, results.size());
  }

  @Test
  public void testGetAll() {
    List<BaleenEntity> entities = getEntityProvider().getAll(0, 1000).collectList().block();
    assertEquals(4, entities.size());
  }

  @Test
  public void testCount() {
    long count = getEntityProvider().count().block();
    assertEquals(4, count);
  }

  @Test
  public void testSearch() {
    EntityFilter filter = new EntityFilter();
    filter.setType("Person");
    EntitySearch search = new EntitySearch(null, filter, null);
    List<BaleenEntity> results = getEntityProvider().search(search, 0, 1000).getResults().collectList().block();;
    assertEquals(2, results.size());
  }

}
