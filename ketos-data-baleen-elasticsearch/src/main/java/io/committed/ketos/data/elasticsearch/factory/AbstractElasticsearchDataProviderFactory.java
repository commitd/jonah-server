package io.committed.ketos.data.elasticsearch.factory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import io.committed.invest.server.data.providers.AbstractDataProviderFactory;
import io.committed.invest.server.data.providers.DataProvider;
import io.committed.invest.server.data.providers.DatabaseConstants;

public abstract class AbstractElasticsearchDataProviderFactory<P extends DataProvider>
    extends AbstractDataProviderFactory<P> {

  protected AbstractElasticsearchDataProviderFactory(final String id, final Class<P> clazz) {
    super(id, clazz, DatabaseConstants.ELASTICSEARCH);
  }

  protected ElasticsearchTemplate buildElasticTemplate(final Map<String, Object> settings)
      throws UnknownHostException {

    final String host = (String) settings.getOrDefault("host", "localhost");
    final int port = (int) settings.getOrDefault("port", 9300);

    final TransportClient transportClient = new PreBuiltTransportClient(Settings.EMPTY)
        .addTransportAddress(
            new InetSocketTransportAddress(InetAddress.getByName(host), port));

    return new ElasticsearchTemplate(transportClient);
  }



}
