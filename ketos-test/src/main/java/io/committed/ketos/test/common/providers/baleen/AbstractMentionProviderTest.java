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
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.output.MentionSearch;
import io.committed.ketos.common.providers.baleen.MentionProvider;

public abstract class AbstractMentionProviderTest {

  public abstract MentionProvider getMentionProvider();

  @Test
  public void testGetByDocument() {
    BaleenDocument doc = new BaleenDocument("a19f6ed4-87bb-4dc6-919e-596761127082", null, "", null);
    List<BaleenMention> mentions = getMentionProvider().getByDocument(doc).collectList().block();
    assertEquals(5, mentions.size());
  }

  @Test
  public void testGetById() {
    BaleenMention mention = getMentionProvider().getById("a8672e25-0f23-43cf-b735-fba8f2d48f3b").block();
    assertNotNull(mention);
    assertEquals("a8672e25-0f23-43cf-b735-fba8f2d48f3b", mention.getId());
  }

  @Test
  public void testGetAll() {
    List<BaleenMention> results = getMentionProvider().getAll(0, 1000).collectList().block();
    assertEquals(5, results.size());
  }

  @Test
  public void testCountByField() {
    List<TermBin> mentions = getMentionProvider()
        .countByField(Optional.empty(), Collections.singletonList("type"), 1000).collectList().block();
    assertEquals(3, mentions.size());
  }

  @Test
  public void testCount() {
    long count = getMentionProvider().count().block();
    assertEquals(5, count);
  }

  @Test
  public void testSearch() {
    MentionFilter filter = new MentionFilter();
    filter.setType("Person");
    MentionSearch search = new MentionSearch(null, filter);
    List<BaleenMention> results = getMentionProvider().search(search, 0, 1000).getResults().collectList().block();
    assertEquals(3, results.size());
  }

}
