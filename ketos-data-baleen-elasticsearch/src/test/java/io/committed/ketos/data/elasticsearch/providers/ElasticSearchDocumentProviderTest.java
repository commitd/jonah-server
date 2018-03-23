package io.committed.ketos.data.elasticsearch.providers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.data.elasticsearch.ElasticsearchTestResource;
import io.committed.ketos.data.elasticsearch.factory.EsDocumentProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractDocumentProviderTest;

public class ElasticSearchDocumentProviderTest extends AbstractDocumentProviderTest {

  private static final ElasticsearchTestResource resource = new ElasticsearchTestResource();

  private DocumentProvider provider;

  @BeforeClass
  public static void beforeClass() {
    resource.setupElastic("documents.json");
  }

  @AfterClass
  public static void afterClass() {
    resource.cleanElastic();
  }

  @Test
  public void testGetDocumentById() {
    BaleenDocument doc = provider.getById("0b0c5261d34b4f43fd1d4945fbf4529ceb6228d04de66d6389ddb65f9d88273c").block();
    assertNotNull(doc);
    assertEquals("0b0c5261d34b4f43fd1d4945fbf4529ceb6228d04de66d6389ddb65f9d88273c", doc.getId());
  }

  @Override
  public DocumentProvider getDocumentProvider() {
    EsDocumentProviderFactory factory = new EsDocumentProviderFactory(new ObjectMapper());
    return (DocumentProvider) factory
        .build(ElasticsearchTestResource.TEST_DB, ElasticsearchTestResource.TEST_DB,
            Collections.singletonMap("index", ElasticsearchTestResource.TEST_DB))
        .block();
  }

}
