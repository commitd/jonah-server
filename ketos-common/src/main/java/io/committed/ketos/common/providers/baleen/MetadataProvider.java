package io.committed.ketos.common.providers.baleen;

import java.util.Optional;

import reactor.core.publisher.Flux;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.extensions.data.providers.DataProvider;

/** Metadata query data provider */
public interface MetadataProvider extends DataProvider {

  @Override
  default String getProviderType() {
    return "MetadataProvider";
  }

  Flux<TermBin> countByKey(Optional<String> key, int size);

  Flux<TermBin> countByValue(Optional<String> key, int size);
}
