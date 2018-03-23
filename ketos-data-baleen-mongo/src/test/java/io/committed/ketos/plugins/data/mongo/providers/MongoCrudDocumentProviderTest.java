package io.committed.ketos.plugins.data.mongo.providers;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.committed.invest.extensions.data.providers.CrudDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.references.BaleenDocumentReference;
import io.committed.ketos.plugins.data.mongo.MongoTestResource;
import io.committed.ketos.plugins.data.mongo.factory.MongoCrudDocumentProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractCrudDocumentProviderTest;

public class MongoCrudDocumentProviderTest
    extends AbstractCrudDocumentProviderTest {

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
  public CrudDataProvider<BaleenDocumentReference, BaleenDocument> getDataProvider() {
    MongoCrudDocumentProviderFactory factory = new MongoCrudDocumentProviderFactory();
    return factory.build("testDataset", "testDatasource", testResource.getSettings()).block();
  }

}
