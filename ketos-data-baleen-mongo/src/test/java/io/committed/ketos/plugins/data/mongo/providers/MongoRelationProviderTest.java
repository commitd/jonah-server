package io.committed.ketos.plugins.data.mongo.providers;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.plugins.data.mongo.MongoTestResource;
import io.committed.ketos.plugins.data.mongo.factory.MongoRelationProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractRelationProviderTest;

public class MongoRelationProviderTest extends AbstractRelationProviderTest {

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
  public RelationProvider getRelationProvider() {
    MongoRelationProviderFactory factory = new MongoRelationProviderFactory();
    return (MongoRelationProvider) factory.build("testDataset", "testDatasource", testResource.getSettings()).block();
  }

}
