package io.committed.ketos.plugins.data.mongo.providers;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.committed.invest.support.data.mongo.AbstractMongoDataProviderFactory;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.plugins.data.mongo.MongoTestResource;
import io.committed.ketos.plugins.data.mongo.factory.MongoDocumentProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractDocumentProviderTest;

public class MongoDocumentProviderTest extends AbstractDocumentProviderTest {

  private static final MongoTestResource testResource = new MongoTestResource();

  @BeforeClass
  public static void beforeClass() {
    testResource.setupMongo("documentProviderTest.json");
  }

  @AfterClass
  public static void afterClass() {
    testResource.clearMongo();
  }

  @Override
  public DocumentProvider getDocumentProvider() {
    MongoDocumentProviderFactory factory = new MongoDocumentProviderFactory();
    return (MongoDocumentProvider) factory.build("testDataset", "testDatasource", getSettings()).block();
  }

  private Map<String, Object> getSettings() {
    Map<String, Object> settings = new HashMap<String, Object>();
    settings.put(AbstractMongoDataProviderFactory.SETTING_DB, MongoTestResource.TEST_DB);
    settings.put(AbstractMongoDataProviderFactory.SETTING_URI, "mongodb://127.0.0.1:27017/");
    return settings;
  }
}
