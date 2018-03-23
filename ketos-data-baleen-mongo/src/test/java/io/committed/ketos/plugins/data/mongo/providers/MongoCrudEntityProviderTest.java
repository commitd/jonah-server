package io.committed.ketos.plugins.data.mongo.providers;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.invest.extensions.data.providers.CrudDataProvider;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.references.BaleenEntityReference;
import io.committed.ketos.plugins.data.mongo.MongoTestResource;
import io.committed.ketos.plugins.data.mongo.factory.MongoCrudEntityProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractCrudDataProviderTest;

public class MongoCrudEntityProviderTest extends AbstractCrudDataProviderTest<BaleenEntityReference, BaleenEntity> {

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

  @Override
  public BaleenEntityReference getDeleteReference() {
    BaleenEntityReference ref = new BaleenEntityReference();
    ref.setDocumentId("a19f6ed4-87bb-4dc6-919e-596761127082");
    ref.setEntityId("42ad69a0-f11b-449e-947f-fe91f52be3d7");
    return ref;
  }

  @Override
  public BaleenEntity getSaveItem() {
    return new BaleenEntity("0bd40743-fbec-4484-b9fc-08d89a916840", "a19f6ed4-87bb-4dc6-919e-596761127082", "Test", "",
        "newval", new PropertiesMap());
  }

}
