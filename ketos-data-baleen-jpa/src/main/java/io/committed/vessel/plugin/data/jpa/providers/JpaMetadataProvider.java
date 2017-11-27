package io.committed.vessel.plugin.data.jpa.providers;

import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.vessel.core.dto.analytic.TermBin;
import io.committed.vessel.plugin.data.jpa.repository.JpaDocumentMetadataRepository;
import io.committed.vessel.server.data.providers.AbstractDataProvider;
import io.committed.vessel.server.data.providers.DatabaseConstants;
import reactor.core.publisher.Flux;

public class JpaMetadataProvider extends AbstractDataProvider implements MetadataProvider {

  private final JpaDocumentMetadataRepository metadata;

  public JpaMetadataProvider(final String dataset, final String datasource,
      final JpaDocumentMetadataRepository metadata) {
    super(dataset, datasource);
    this.metadata = metadata;
  }



  @Override
  public String getDatabase() {
    return DatabaseConstants.SQL;
  }



  @Override
  public Flux<TermBin> countByKey() {
    return Flux.fromStream(metadata.countByKey());
  }



  @Override
  public Flux<TermBin> countByKey(final String key) {
    return Flux.fromStream(metadata.countByKey(key));
  }



  @Override
  public Flux<TermBin> countByValue() {
    return Flux.fromStream(metadata.countByValue());
  }



  @Override
  public Flux<TermBin> countByValue(final String key) {
    return Flux.fromStream(metadata.countByValue(key));
  }



}
