package io.committed.ketos.common.providers.baleen;

import io.committed.vessel.core.dto.analytic.TermBin;
import io.committed.vessel.server.data.providers.DataProvider;
import reactor.core.publisher.Flux;

public interface MetadataProvider extends DataProvider {

  @Override
  default String getProviderType() {
    return "MetadataProvider";
  }

  Flux<TermBin> countByKey();

  Flux<TermBin> countByKey(String key);

  Flux<TermBin> countByValue();

  Flux<TermBin> countByValue(String key);

}
