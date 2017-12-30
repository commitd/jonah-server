package io.committed.ketos.data.jpa.providers;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.jpa.AbstractJpaDataProvider;
import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.data.jpa.repository.JpaDocumentMetadataRepository;
import reactor.core.publisher.Flux;

public class JpaMetadataProvider extends AbstractJpaDataProvider implements MetadataProvider {

  private final JpaDocumentMetadataRepository metadata;

  public JpaMetadataProvider(final String dataset, final String datasource,
      final JpaDocumentMetadataRepository metadata) {
    super(dataset, datasource);
    this.metadata = metadata;
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
