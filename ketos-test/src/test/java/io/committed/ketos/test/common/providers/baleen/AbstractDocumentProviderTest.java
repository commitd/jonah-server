package io.committed.ketos.test.common.providers.baleen;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import io.committed.invest.core.constants.TimeInterval;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.analytic.TimeRange;
import io.committed.ketos.common.constants.ItemTypes;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.general.NamedGeoLocation;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentFilter.DocumentInfoFilter;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import io.committed.ketos.common.graphql.output.DocumentSearch;
import io.committed.ketos.common.providers.baleen.DocumentProvider;

public abstract class AbstractDocumentProviderTest {

  @Test
  public void testGetById() {
    BaleenDocument doc =
        getDocumentProvider().getById("a19f6ed4-87bb-4dc6-919e-596761127082").block();
    assertEquals("a19f6ed4-87bb-4dc6-919e-596761127082", doc.getId());
  }

  @Test
  public void testCount() {
    long count = getDocumentProvider().count().block();
    assertEquals(4, count);
  }

  @Test
  public void testGetAll() {
    List<BaleenDocument> results = getDocumentProvider().getAll(0, 10).collectList().block();
    assertEquals(4, results.size());
  }

  @Test
  public void testGetAllWithSize() {
    List<BaleenDocument> results = getDocumentProvider().getAll(0, 1).collectList().block();
    assertEquals(1, results.size());
  }

  @Test
  public void testSearch() {
    DocumentFilter filter = new DocumentFilter();
    filter.setId("a19f6ed4-87bb-4dc6-919e-596761127082");
    DocumentSearch search = new DocumentSearch(null, filter, null, null, null);
    DocumentSearchResult results = getDocumentProvider().search(search, 0, 4);
    List<BaleenDocument> resultList = results.getResults().collectList().block();
    assertEquals(1, resultList.size());
    assertEquals("a19f6ed4-87bb-4dc6-919e-596761127082", resultList.get(0).getId());
  }

  @Test
  public void testDocumentInfoFilter() {
    DocumentFilter filter = new DocumentFilter();
    DocumentInfoFilter infoFilter = new DocumentInfoFilter();
    infoFilter.setLanguage("x-unspecified");
    filter.setInfo(infoFilter);
    DocumentSearch search = new DocumentSearch(null, filter, null, null, null);
    List<BaleenDocument> results =
        getDocumentProvider().search(search, 0, 1000).getResults().collectList().block();
    assertEquals(1, results.size());
  }

  // @Test
  // public void testDocumentMetadataFilter() {
  // DocumentFilter filter = new DocumentFilter();
  // Map<String, Object> properties = new HashMap<String, Object>();
  // properties.put("producer", "Skia/PDF m58");
  // filter.setMetadata(new PropertiesList(properties));
  // DocumentSearch search = new DocumentSearch(null, filter, null, null, null);
  // List<BaleenDocument> results = getDocumentProvider().search(search, 0,
  // 1000).getResults().collectList().block();
  // assertEquals(1, results.size());
  // }

  @Test
  public void testSearchWithEntities() {
    EntityFilter filter = new EntityFilter();
    filter.setType("Person");
    MentionFilter menFilter = new MentionFilter();
    menFilter.setType("Person");
    DocumentFilter docFilter = new DocumentFilter();
    docFilter.setId("a19f6ed4-87bb-4dc6-919e-596761127082");
    DocumentSearch search =
        new DocumentSearch(
            null,
            docFilter,
            Collections.singletonList(menFilter),
            Collections.singletonList(filter),
            null);
    List<BaleenDocument> results =
        getDocumentProvider().search(search, 0, 1000).getResults().collectList().block();
    assertEquals(1, results.size());
  }

  @Test
  public void testCountByField() {
    List<TermBin> bins =
        getDocumentProvider()
            .countByField(Optional.empty(), Collections.singletonList("externalId"), 4)
            .collectList()
            .block();

    assertEquals(4, bins.size());
    for (TermBin bin : bins) {
      assertEquals(1, bin.getCount());
    }
  }

  @Test
  public void testCountByDate() {
    List<TimeBin> hours =
        getDocumentProvider()
            .countByDate(Optional.empty(), TimeInterval.HOUR)
            .collectList()
            .block();
    assertEquals(1, hours.size());
  }

  @Test
  public void testDocumentTimeRange() {
    TimeRange block = getDocumentProvider().documentTimeRange(Optional.empty()).block();
    assertEquals(107000, block.getDuration());
    assertEquals(TimeInterval.SECOND, block.getInterval());
  }

  // @Test
  // public void testEntityTimeRange() {
  // TimeRange block = getDocumentProvider().entityTimeRange(Optional.empty()).block();
  // assertNotNull(block);
  // }
  //
  // @Test
  // public void testCountByJoinDate() {
  // List<TimeBin> results =
  // getDocumentProvider().countByJoinedDate(Optional.empty(), ItemTypes.MENTION,
  // TimeInterval.HOUR).collectList()
  // .block();
  // assertTrue(!results.isEmpty());
  // }

  @Test
  public void testDocumentLocations() {
    DocumentFilter filter = new DocumentFilter();
    filter.setId("a19f6ed4-87bb-4dc6-919e-596761127082");
    List<NamedGeoLocation> locations =
        getDocumentProvider().documentLocations(Optional.empty(), 1000).collectList().block();
    assertEquals(1, locations.size());
  }

  @Test
  public void testCountByJoinedField() {
    List<TermBin> results =
        getDocumentProvider()
            .countByJoinedField(
                Optional.empty(), ItemTypes.ENTITY, Collections.singletonList("type"), 1000)
            .collectList()
            .block();
    assertEquals(3, results.size());

    List<TermBin> results2 =
        getDocumentProvider()
            .countByJoinedField(
                Optional.empty(), ItemTypes.MENTION, Collections.singletonList("type"), 1000)
            .collectList()
            .block();
    assertEquals(3, results2.size());

    List<TermBin> results3 =
        getDocumentProvider()
            .countByJoinedField(
                Optional.empty(), ItemTypes.RELATION, Collections.singletonList("type"), 1000)
            .collectList()
            .block();
    assertEquals(1, results3.size());
  }

  public abstract DocumentProvider getDocumentProvider();
}
