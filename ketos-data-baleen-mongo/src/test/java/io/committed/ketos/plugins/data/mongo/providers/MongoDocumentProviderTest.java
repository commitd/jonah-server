package io.committed.ketos.plugins.data.mongo.providers;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.plugins.data.mongo.MongoTestResource;
import io.committed.ketos.plugins.data.mongo.factory.MongoDocumentProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractDocumentProviderTest;

public class MongoDocumentProviderTest extends AbstractDocumentProviderTest {

  private static final MongoTestResource testResource = new MongoTestResource();

  @BeforeClass
  public static void beforeClass() {
    testResource.setupMongo("documents.json");
  }

  @AfterClass
  public static void afterClass() {
    testResource.clearMongo();
  }

  @Override
  public DocumentProvider getDocumentProvider() {
    MongoDocumentProviderFactory factory = new MongoDocumentProviderFactory();
    return (MongoDocumentProvider) factory.build("testDataset", "testDatasource", testResource.getSettings()).block();
  }
}
