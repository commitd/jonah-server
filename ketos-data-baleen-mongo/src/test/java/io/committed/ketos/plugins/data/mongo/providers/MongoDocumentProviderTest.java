package io.committed.ketos.plugins.data.mongo.providers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.plugins.data.mongo.AbstractMongoResourceTest;
import io.committed.ketos.plugins.data.mongo.factory.MongoDocumentProviderFactory;

public class MongoDocumentProviderTest extends AbstractMongoResourceTest {

  private MongoDocumentProvider documentProvider;

  public MongoDocumentProviderTest() {
    MongoDocumentProviderFactory factory = new MongoDocumentProviderFactory();
    documentProvider =
        (MongoDocumentProvider) factory.build("testDataset", "testDatasource", getSettings()).block();
  }

  @Test
  public void testGetById() {
    BaleenDocument doc =
        documentProvider.getById("402da4330a8ac77d0b250fe35c43a98b76c7876cbc00bba8df95832cefac1c4d").block();
    assertEquals("402da4330a8ac77d0b250fe35c43a98b76c7876cbc00bba8df95832cefac1c4d", doc.getId());
    assertEquals(" \n \n", doc.getContent());
  }

  @Override
  protected String getResourcePath() {
    return "documentProviderTest.json";
  }
}
