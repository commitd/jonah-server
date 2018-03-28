package io.committed.ketos.plugins.data.mongo.providers;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.plugins.data.mongo.MongoTestResource;
import io.committed.ketos.plugins.data.mongo.factory.MongoMentionProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractMentionProviderTest;

public class MongoMentionProviderTest extends AbstractMentionProviderTest {

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
  public MentionProvider getMentionProvider() {
    MongoMentionProviderFactory factory = new MongoMentionProviderFactory();
    return (MongoMentionProvider)
        factory.build("testDataset", "testDatasource", testResource.getSettings()).block();
  }
}
