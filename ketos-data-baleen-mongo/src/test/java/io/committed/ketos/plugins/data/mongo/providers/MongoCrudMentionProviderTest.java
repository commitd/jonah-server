package io.committed.ketos.plugins.data.mongo.providers;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.committed.invest.extensions.data.providers.CrudDataProvider;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.references.BaleenMentionReference;
import io.committed.ketos.plugins.data.mongo.MongoTestResource;
import io.committed.ketos.plugins.data.mongo.factory.MongoCrudMentionProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractCrudMentionProviderTest;

public class MongoCrudMentionProviderTest extends AbstractCrudMentionProviderTest {

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
  public CrudDataProvider<BaleenMentionReference, BaleenMention> getDataProvider() {
    MongoCrudMentionProviderFactory factory = new MongoCrudMentionProviderFactory();
    return factory.build("testDataset", "testDatasource", testResource.getSettings()).block();
  }
}
