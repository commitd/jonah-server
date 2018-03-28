package io.committed.ketos.data.elasticsearch;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.reindex.ReindexPlugin;
import org.elasticsearch.join.ParentJoinPlugin;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.percolator.PercolatorPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.mustache.MustachePlugin;
import org.elasticsearch.transport.Netty4Plugin;

public class EmbeddedElasticsearch5 implements AutoCloseable {

  private final Node node;
  private final Path dataPath;
  private final int httpPort;
  private final int transportPort;
  private final String clusterName;

  public EmbeddedElasticsearch5() throws NodeValidationException, IOException {
    this(
        Files.createTempDirectory("elasticsearch"),
        "test-cluster",
        generateRandomPort(),
        generateRandomPort());
  }

  public EmbeddedElasticsearch5(
      final Path dataPath, final String clusterName, final int httpPort, final int transportPort)
      throws NodeValidationException {
    this.clusterName = clusterName;
    this.httpPort = httpPort;
    this.transportPort = transportPort;
    this.dataPath = dataPath;

    // NB 'transport.type' is not 'local' as connecting via separate transport client
    final Settings settings =
        Settings.builder()
            .put("path.home", dataPath.toString())
            .put("cluster.name", clusterName)
            .put("http.port", Integer.toString(httpPort))
            .put("transport.tcp.port", Integer.toString(transportPort))
            .put("http.enabled", true)
            .build();

    List<Class<? extends Plugin>> plugins = new ArrayList<>();
    plugins.add(Netty4Plugin.class);
    plugins.add(ReindexPlugin.class);
    plugins.add(PercolatorPlugin.class);
    plugins.add(MustachePlugin.class);
    plugins.add(ParentJoinPlugin.class);

    node = new PluginConfigurableNode(settings, plugins);
    node.start();

    node.client().admin().cluster().prepareHealth().setWaitForGreenStatus().execute().actionGet();
    System.out.println("ES is ready");
  }

  private static class PluginConfigurableNode extends Node {
    PluginConfigurableNode(
        final Settings settings, final Collection<Class<? extends Plugin>> classpathPlugins) {
      super(InternalSettingsPreparer.prepareEnvironment(settings, null), classpathPlugins);
    }
  }

  /**
   * Flush the index to ensure data is written before testing
   *
   * @param index
   */
  public void flush(final String index) {
    client().admin().indices().refresh(new RefreshRequest(index)).actionGet();
  }

  /** @return a client for the cluster */
  public Client client() {
    return node.client();
  }

  /** @return the cluster name */
  public String getClusterName() {
    return clusterName;
  }

  /** @return the http port */
  public int getHttpPort() {
    return httpPort;
  }

  /** @return the transport port */
  public int getTransportPort() {
    return transportPort;
  }

  /** @return the http port url */
  public String getHttpUrl() {
    return "http://localhost:" + httpPort;
  }

  /** @return the transport url */
  public String getTransportUrl() {
    return "http://localhost:" + transportPort;
  }

  @Override
  public void close() throws IOException {
    node.close();
    FileUtils.deleteDirectory(dataPath.toFile());
  }

  public static int generateRandomPort() throws IOException {
    try (ServerSocket serverSocket = new ServerSocket(0)) {
      return serverSocket.getLocalPort();
    }
  }
}
