package io.committed.ketos.common.graphql.intermediate;

import io.committed.ketos.common.data.BaleenEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntitySearchResult {

  private Flux<BaleenEntity> results;

  private Mono<Long> total;

  private long offset;

  private long size;
}
