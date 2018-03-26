package io.committed.ketos.plugins.data.mongo.providers;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.committed.invest.extensions.data.providers.CrudDataProvider;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.references.BaleenEntityReference;
import io.committed.ketos.plugins.data.mongo.MongoTestResource;
import io.committed.ketos.plugins.data.mongo.factory.MongoCrudEntityProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractCrudEntityProviderTest;

public class MongoCrudEntityProviderTest extends AbstractCrudEntityProviderTest {

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
  public CrudDataProvider<BaleenEntityReference, BaleenEntity> getDataProvider() {
    MongoCrudEntityProviderFactory factory = new MongoCrudEntityProviderFactory();
    return factory.build("testDataset", "testDatasource", testResource.getSettings()).block();
  }
}
