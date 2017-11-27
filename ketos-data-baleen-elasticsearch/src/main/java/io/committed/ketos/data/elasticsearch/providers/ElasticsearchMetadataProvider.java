package io.committed.ketos.data.elasticsearch.providers;

import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.data.elasticsearch.repository.EsMetadataService;
import io.committed.vessel.core.dto.analytic.TermBin;
import io.committed.vessel.server.data.providers.AbstractDataProvider;
import io.committed.vessel.server.data.providers.DatabaseConstants;
import reactor.core.publisher.Flux;

public class ElasticsearchMetadataProvider extends AbstractDataProvider
    implements MetadataProvider {

  private final EsMetadataService metadataService;

  public ElasticsearchMetadataProvider(final String dataset, final String datasource,
      final EsMetadataService metadataService) {
    super(dataset, datasource);
    this.metadataService = metadataService;
  }


  @Override
  public String getDatabase() {
    return DatabaseConstants.ELASTICSEARCH;
  }


  @Override
  public Flux<TermBin> countByKey() {
    return metadataService.countByKey(null);
  }


  @Override
  public Flux<TermBin> countByKey(final String key) {
    return metadataService.countByKey(key);
  }


  @Override
  public Flux<TermBin> countByValue() {
    return metadataService.countByKey(null);

  }


  @Override
  public Flux<TermBin> countByValue(final String key) {
    return metadataService.countByValue(key);

  }


}
