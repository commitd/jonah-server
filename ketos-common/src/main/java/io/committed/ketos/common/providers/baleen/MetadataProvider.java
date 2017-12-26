package io.committed.ketos.common.providers.baleen;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.server.data.providers.DataProvider;
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
