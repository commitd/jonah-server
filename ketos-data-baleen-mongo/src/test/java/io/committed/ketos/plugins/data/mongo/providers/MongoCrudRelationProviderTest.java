package io.committed.ketos.plugins.data.mongo.providers;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.invest.extensions.data.providers.CrudDataProvider;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.references.BaleenRelationReference;
import io.committed.ketos.plugins.data.mongo.MongoTestResource;
import io.committed.ketos.plugins.data.mongo.factory.MongoCrudRelationProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractCrudDataProviderTest;

public class MongoCrudRelationProviderTest
    extends AbstractCrudDataProviderTest<BaleenRelationReference, BaleenRelation> {


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

  @Override
  public BaleenRelationReference getDeleteReference() {
    BaleenRelationReference rel = new BaleenRelationReference();
    rel.setDocumentId("a19f6ed4-87bb-4dc6-919e-596761127082");
    rel.setRelationId("9dec8723-5ba5-4fb1-8718-6c12586aaa89");
    return rel;
  }

  @Override
  public BaleenRelation getSaveItem() {
    BaleenRelation relation = new BaleenRelation("9dec8723-5ba5-4fb1-8718-6c12586aaa89",
        "a19f6ed4-87bb-4dc6-919e-596761127082", 0, 1, "test", "test", "value", null, null, new PropertiesMap());
    return relation;
  }

}
