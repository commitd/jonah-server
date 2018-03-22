package io.committed.ketos.data.elasticsearch.providers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.data.elasticsearch.AbstractElasticsearchTest;
import io.committed.ketos.data.elasticsearch.factory.EsDocumentProviderFactory;

public class ElasticSearchDocumentProviderTest extends AbstractElasticsearchTest {

  private DocumentProvider provider;

  @Override
  protected String getResourcePath() {
    return "documents.json";
  }

  public ElasticSearchDocumentProviderTest() {
    EsDocumentProviderFactory factory = new EsDocumentProviderFactory(new ObjectMapper());
    provider = (DocumentProvider) factory
        .build(AbstractElasticsearchTest.TEST_DB, TEST_DB, Collections.singletonMap("index", TEST_DB)).block();
  }

  @Test
  public void testGetDocumentById() {
    BaleenDocument doc = provider.getById("0b0c5261d34b4f43fd1d4945fbf4529ceb6228d04de66d6389ddb65f9d88273c").block();
    assertNotNull(doc);
    assertEquals("0b0c5261d34b4f43fd1d4945fbf4529ceb6228d04de66d6389ddb65f9d88273c", doc.getId());
  }

}
