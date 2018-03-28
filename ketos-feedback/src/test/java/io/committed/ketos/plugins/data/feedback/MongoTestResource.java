package io.committed.ketos.plugins.data.feedback;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.MongoClient;

import io.committed.invest.support.data.mongo.AbstractMongoDataProviderFactory;

public class MongoTestResource {

  public static final String TEST_DB = "testDB";

  private MongoClient client;

  public void setupMongo() {
    client = new MongoClient("127.0.0.1");
  }

  public void clearMongo() {
    client.getDatabase(TEST_DB).drop();
  }

  public Map<String, Object> getSettings() {
    final Map<String, Object> settings = new HashMap<String, Object>();
    settings.put(AbstractMongoDataProviderFactory.SETTING_DB, MongoTestResource.TEST_DB);
    settings.put(AbstractMongoDataProviderFactory.SETTING_URI, "mongodb://127.0.0.1:27017/");
    return settings;
  }
}
