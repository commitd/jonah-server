package io.committed.ketos.data.elasticsearch.providers;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.data.elasticsearch.ElasticsearchTestResource;
import io.committed.ketos.data.elasticsearch.factory.EsDocumentProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractDocumentProviderTest;

public class ElasticSearchDocumentProviderTest extends AbstractDocumentProviderTest {

  private static final ElasticsearchTestResource resource = new ElasticsearchTestResource();

  @BeforeClass
  public static void beforeClass() {
    resource.setupElastic("documents.json");
  }

  @AfterClass
  public static void afterClass() {
    resource.cleanElastic();
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
