package io.committed.ketos.plugins.data.mongo.providers;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.plugins.data.mongo.MongoTestResource;
import io.committed.ketos.plugins.data.mongo.factory.MongoMetadataProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractMetadataProviderTest;

public class MongoMetadataProviderTest extends AbstractMetadataProviderTest {

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
  public MetadataProvider getMetadataProvider() {
    MongoMetadataProviderFactory factory = new MongoMetadataProviderFactory();
    return (MongoMetadataProvider)
        factory.build("testDataset", "testDatasource", testResource.getSettings()).block();
  }
}
