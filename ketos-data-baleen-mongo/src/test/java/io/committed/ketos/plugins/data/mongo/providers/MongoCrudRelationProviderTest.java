package io.committed.ketos.plugins.data.mongo.providers;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.committed.invest.extensions.data.providers.CrudDataProvider;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.references.BaleenRelationReference;
import io.committed.ketos.plugins.data.mongo.MongoTestResource;
import io.committed.ketos.plugins.data.mongo.factory.MongoCrudRelationProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractCrudRelationProviderTest;

public class MongoCrudRelationProviderTest extends AbstractCrudRelationProviderTest {

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
  public CrudDataProvider<BaleenRelationReference, BaleenRelation> getDataProvider() {
    MongoCrudRelationProviderFactory factory = new MongoCrudRelationProviderFactory();
    return factory.build("testDataset", "testDatasource", testResource.getSettings()).block();
  }

}
