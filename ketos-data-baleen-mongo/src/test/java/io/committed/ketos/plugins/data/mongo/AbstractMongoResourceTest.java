package io.committed.ketos.plugins.data.mongo;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.Document;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;

import io.committed.invest.support.data.mongo.AbstractMongoDataProviderFactory;

public abstract class AbstractMongoResourceTest {

  private static final String TEST_DB = "testDB";

  private static MongoClient client;

  private ObjectMapper mapper;

  public AbstractMongoResourceTest() {
    mapper = new ObjectMapper();
  }

  @BeforeClass
  public static void setupClass() {
    client = MongoClients.create("mongodb://127.0.0.1:27017");
  }

  @AfterClass
  public static void teardownClass() {
    client.getDatabase(TEST_DB).drop((result, throwable) -> {
      client.close();
    });
  }

  @Before
  public void setup() {
    loadResource();
  }

  protected Map<String, Object> getSettings() {
    Map<String, Object> settings = new HashMap<String, Object>();
    settings.put(AbstractMongoDataProviderFactory.SETTING_DB, TEST_DB);
    settings.put(AbstractMongoDataProviderFactory.SETTING_URI, "mongodb://127.0.0.1:27017/");
    return settings;
  }

  protected void loadResource() {
    Map<String, List<Map<String, Object>>> value = null;
    try (InputStream resource = getClass().getClassLoader().getResourceAsStream(getResourcePath())) {
      value = mapper.readValue(resource, new TypeReference<HashMap<String, List<Map<String, Object>>>>() {});
    } catch (IOException e) {
      fail("Exception when loading test resource:\n" + e.getMessage());
    }

    if (value == null) {
      fail("No suitable test data found in file");
    }

    MongoDatabase db = client.getDatabase(TEST_DB);
    if (value.get("entities") != null) {
      load(db, "entities", value.get("entities"));
    }
    if (value.get("relations") != null) {
      load(db, "relations", value.get("relations"));
    }
    if (value.get("documents") != null) {
      load(db, "documents", value.get("documents"));
    }
    if (value.get("mentions") != null) {
      load(db, "mentions", value.get("mentions"));
    }
  }

  protected abstract String getResourcePath();

  private void load(MongoDatabase db, String collection, List<Map<String, Object>> values) {
    db.getCollection(collection).insertMany(toDocuments(values), null);
  }

  private List<Document> toDocuments(List<Map<String, Object>> values) {
    return values.stream().map(o -> new Document(o)).collect(Collectors.toCollection(ArrayList::new));
  }

}
