package io.committed.ketos.common.graphql.intermediate;

import io.committed.ketos.common.data.BaleenDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@Builder
@AllArgsConstructor
public class DocumentSearchResult {

  private Flux<BaleenDocument> results;

  private Mono<Long> total;
}
