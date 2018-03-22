package io.committed.ketos.data.elasticsearch;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractElasticsearchTest {

  public static final String TEST_DB = "testdb";

  private static Client client;

  private ObjectMapper mapper;

  @BeforeClass
  public static void beforeClass() {
    Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();

    InetSocketTransportAddress inetSocketAddress;
    try {
      inetSocketAddress = new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300);
      client = new PreBuiltTransportClient(settings).addTransportAddress(inetSocketAddress);
    } catch (UnknownHostException e) {
      fail("Error creating elastic client");
    }
  }

  @Before
  public void before() {
    mapper = new ObjectMapper();
    loadResource();
  }

  @After
  public void after() {
    DeleteIndexRequest request = new DeleteIndexRequest(TEST_DB);
    client.admin().indices().delete(request).actionGet();
  }

  protected Client createElasticClient() throws UnknownHostException {
    Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();

    InetSocketTransportAddress inetSocketAddress =
        new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300);
    return new PreBuiltTransportClient(settings).addTransportAddress(inetSocketAddress);
  }

  protected void loadResource() {
    Map<String, List<Map<String, Object>>> value = null;
    try (InputStream resource = getClass().getClassLoader().getResourceAsStream(getResourcePath())) {
      value = mapper.readValue(resource, new TypeReference<HashMap<String, List<Map<String, Object>>>>() {});
    } catch (IOException e) {
      fail("Exception when loading test resource:\n" + e.getMessage());
    }

    if (value.get("documents") != null) {
      loadMappings("document");
      load(client, TEST_DB, "document", value.get("documents"));
    }
    if (value.get("entities") != null) {
      loadMappings("entity");
      load(client, TEST_DB, "entity", value.get("entities"));
    }
    if (value.get("mentions") != null) {
      loadMappings("mention");
      load(client, TEST_DB, "mention", value.get("mentions"));
    }
    if (value.get("relation") != null) {
      loadMappings("relation");
      load(client, TEST_DB, "relation", value.get("relations"));
    }

    client.admin().indices().refresh(new RefreshRequest(TEST_DB)).actionGet();
  }

  private void loadMappings(String type) {
    CreateIndexRequestBuilder prepareBuilder = client.admin().indices().prepareCreate("testdb");
    Map<String, Object> mapping = null;
    try (InputStream resource = getClass().getClassLoader().getResourceAsStream(type + "Mapping.json")) {
      mapping = mapper.readValue(resource, new TypeReference<HashMap<String, Map<String, Object>>>() {});
    } catch (IOException e) {
      fail("Exception when loading test resource:\n" + e.getMessage());
    }
    prepareBuilder.addMapping(type, mapping).execute().actionGet();
  }

  private void load(Client client, String db, String type, List<Map<String, Object>> values) {
    for (Map<String, Object> item : values) {
      client.prepareIndex(db, type).setRefreshPolicy(RefreshPolicy.IMMEDIATE)
          .setSource(toJson(item), XContentType.JSON)
          .get();
    }
  }

  protected String toJson(Map<String, Object> value) {
    try {
      return mapper.writeValueAsString(value);
    } catch (IOException e) {
      System.out.println(e);
      fail("Failed to create json from resource map");
    }
    return "";
  }

  protected abstract String getResourcePath();

}
