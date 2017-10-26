package io.committed.ketos.plugins.graphql.baleenservices.providers;

import io.committed.ketos.plugins.data.baleen.BaleenDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DocumentProvider extends DataProvider {

  Mono<BaleenDocument> getById(String id);

  Flux<BaleenDocument> search(String search, int limit);

  Flux<BaleenDocument> all(int limit);

}
