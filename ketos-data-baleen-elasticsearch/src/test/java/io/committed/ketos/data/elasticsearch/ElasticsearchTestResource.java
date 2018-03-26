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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ElasticsearchTestResource {

  public static final String TEST_DB = "testdb";

  private static Client client;

  private ObjectMapper mapper;

  private EmbeddedElasticsearch5 elasticsearch;

  public void setupElastic(final String resourcePath) {
    try {
      elasticsearch = new EmbeddedElasticsearch5();

      client = createElasticClient();
    } catch (final Exception e1) {
      throw new RuntimeException(e1);
    }


    mapper = new ObjectMapper();
    loadResource(resourcePath);
  }

  public void cleanElastic() {
    if (client != null) {
      final DeleteIndexRequest request = new DeleteIndexRequest(TEST_DB);
      client.admin().indices().delete(request).actionGet();
    }

    try {
      if (elasticsearch != null) {
        elasticsearch.close();
      }
    } catch (final Exception e) {
      throw new RuntimeException();
    }
  }

  private Client createElasticClient() throws UnknownHostException {
    final Settings settings = Settings.builder().put("cluster.name", elasticsearch.getClusterName()).build();

    final InetSocketTransportAddress inetSocketAddress =
        new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), elasticsearch.getTransportPort());
    return new PreBuiltTransportClient(settings).addTransportAddress(inetSocketAddress);
  }

  protected void loadResource(final String resourcePath) {
    Map<String, List<Map<String, Object>>> value = null;
    try (InputStream resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
      value = mapper.readValue(resource, new TypeReference<HashMap<String, List<Map<String, Object>>>>() {});
    } catch (final IOException e) {
      fail("Exception when loading test resource:\n" + e.getMessage());
    }

    loadMappings();
    if (value.get("documents") != null) {
      load(client, TEST_DB, "document", value.get("documents"));
    }
    if (value.get("entities") != null) {
      loadWithParent(client, TEST_DB, "entity", value.get("entities"));
    }
    if (value.get("mentions") != null) {
      loadWithParent(client, TEST_DB, "mention", value.get("mentions"));
    }
    if (value.get("relations") != null) {
      loadWithParent(client, TEST_DB, "relation", value.get("relations"));
    }

    client.admin().indices().refresh(new RefreshRequest(TEST_DB)).actionGet();
  }

  private void loadMappings() {
    final CreateIndexRequestBuilder prepareBuilder = client.admin().indices().prepareCreate("testdb");
    prepareBuilder.addMapping("document", getMapping("document"));
    prepareBuilder.addMapping("entity", getMapping("entity"));
    prepareBuilder.addMapping("mention", getMapping("mention"));
    prepareBuilder.addMapping("relation", getMapping("relation"));
    prepareBuilder.execute().actionGet();
  }

  private Map<String, Object> getMapping(String type) {
    Map<String, Object> mapping = null;
    try (InputStream resource = getClass().getClassLoader().getResourceAsStream(type + "Mapping.json")) {
      mapping = mapper.readValue(resource, new TypeReference<HashMap<String, Map<String, Object>>>() {});
    } catch (final IOException e) {
      fail("Exception when loading test resource:\n" + e.getMessage());
    }
    return mapping;
  }

  private void load(final Client client, final String db, final String type, final List<Map<String, Object>> values) {
    for (final Map<String, Object> item : values) {
      client.prepareIndex(db, type).setRefreshPolicy(RefreshPolicy.IMMEDIATE)
          .setSource(toJson(item), XContentType.JSON)
          .get();
    }
  }

  private void loadWithParent(final Client client, final String db, final String type,
      final List<Map<String, Object>> values) {
    for (final Map<String, Object> item : values) {
      client.prepareIndex(db, type).setRefreshPolicy(RefreshPolicy.IMMEDIATE)
          .setParent((String) item.get("docId"))
          .setRouting((String) item.get("docId"))
          .setSource(toJson(item), XContentType.JSON)
          .get();
    }
  }

  protected String toJson(final Map<String, Object> value) {
    try {
      return mapper.writeValueAsString(value);
    } catch (final IOException e) {
      System.out.println(e);
      fail("Failed to create json from resource map");
    }
    return "";
  }

  public int getPort() {
    return elasticsearch.getTransportPort();
  }

  public String getClusterName() {
    return elasticsearch.getClusterName();
  }

}
