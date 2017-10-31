package io.committed.ketos.common.providers.baleen;

import io.committed.ketos.common.data.BaleenDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DocumentProvider extends DataProvider {

  Mono<BaleenDocument> getById(String id);

  Flux<BaleenDocument> search(String search, int limit);

  Flux<BaleenDocument> all(int limit);

  @Override
  default String getProviderType() {
    return "DocumentProvider";
  }

}
