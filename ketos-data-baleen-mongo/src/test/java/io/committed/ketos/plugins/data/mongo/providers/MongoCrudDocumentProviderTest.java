package io.committed.ketos.plugins.data.mongo.providers;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.invest.extensions.data.providers.CrudDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.references.BaleenDocumentReference;
import io.committed.ketos.plugins.data.mongo.MongoTestResource;
import io.committed.ketos.plugins.data.mongo.factory.MongoCrudDocumentProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractCrudDataProviderTest;

public class MongoCrudDocumentProviderTest
    extends AbstractCrudDataProviderTest<BaleenDocumentReference, BaleenDocument> {

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

  @Override
  public BaleenDocumentReference getDeleteReference() {
    BaleenDocumentReference ref = new BaleenDocumentReference();
    ref.setDocumentId("575f6e573aaa400bd69f6c282ced6c81969aff20abe96be4ac8989f1f74ef55b");
    return ref;
  }

  @Override
  public BaleenDocument getSaveItem() {
    BaleenDocument document = new BaleenDocument("fd683817e7b32a1beae34d5a166f26d78830973c27a07eec7877760711993add",
        Collections.EMPTY_LIST, "", new PropertiesMap());
    return document;
  }

}
