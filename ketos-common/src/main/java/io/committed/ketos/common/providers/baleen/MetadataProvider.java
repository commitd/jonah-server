package io.committed.ketos.common.providers.baleen;

import java.util.Optional;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.extensions.data.providers.DataProvider;
import reactor.core.publisher.Flux;

/**
 * Metadata query data provider
 *
 */
public interface MetadataProvider extends DataProvider {

  @Override
  default String getProviderType() {
    return "MetadataProvider";
  }

  Flux<TermBin> countByKey(Optional<String> key, int size);

  Flux<TermBin> countByValue(Optional<String> key, int size);

}
