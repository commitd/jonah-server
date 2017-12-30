package io.committed.ketos.data.elasticsearch.providers;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.data.elasticsearch.repository.EsMetadataService;
import reactor.core.publisher.Flux;

public class ElasticsearchMetadataProvider
    extends AbstractElasticsearchServiceDataProvider<EsMetadataService>
    implements MetadataProvider {

  public ElasticsearchMetadataProvider(final String dataset, final String datasource,
      final EsMetadataService metadataService) {
    super(dataset, datasource, metadataService);
  }

  @Override
  public Flux<TermBin> countByKey() {
    return getService().countByKey(null);
  }

  @Override
  public Flux<TermBin> countByKey(final String key) {
    return getService().countByKey(key);
  }

  @Override
  public Flux<TermBin> countByValue() {
    return getService().countByKey(null);

  }

  @Override
  public Flux<TermBin> countByValue(final String key) {
    return getService().countByValue(key);

  }


}
