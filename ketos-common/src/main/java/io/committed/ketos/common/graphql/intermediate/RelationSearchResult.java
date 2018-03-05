package io.committed.ketos.common.graphql.intermediate;

import io.committed.ketos.common.data.BaleenRelation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@Builder
@AllArgsConstructor
public class RelationSearchResult {

  private Flux<BaleenRelation> results;

  private Mono<Long> total;

  private long offset;

  private long size;
}
