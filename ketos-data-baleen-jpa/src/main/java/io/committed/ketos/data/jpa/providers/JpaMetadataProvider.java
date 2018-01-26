package io.committed.ketos.data.jpa.providers;

import java.util.Optional;
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
  public Flux<TermBin> countByKey(final Optional<String> key, final int size) {
    if (key.isPresent()) {
      return Flux.fromStream(metadata.countByKey(key.get(), size));
    } else {
      return Flux.fromStream(metadata.countByKey(size));
    }
  }

  @Override
  public Flux<TermBin> countByValue(final Optional<String> key, final int size) {
    if (key.isPresent()) {
      return Flux.fromStream(metadata.countByValue(key.get(), size));
    } else {
      return Flux.fromStream(metadata.countByValue(size));
    }
  }

}
