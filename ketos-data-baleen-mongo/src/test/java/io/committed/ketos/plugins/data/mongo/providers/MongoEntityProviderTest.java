package io.committed.ketos.plugins.data.mongo.providers;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.plugins.data.mongo.MongoTestResource;
import io.committed.ketos.plugins.data.mongo.factory.MongoEntityProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractEntityProviderTest;

public class MongoEntityProviderTest extends AbstractEntityProviderTest {

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
  public EntityProvider getEntityProvider() {
    MongoEntityProviderFactory factory = new MongoEntityProviderFactory();
    return (MongoEntityProvider) factory.build("testDataset", "testDatasource", testResource.getSettings()).block();
  }

}
