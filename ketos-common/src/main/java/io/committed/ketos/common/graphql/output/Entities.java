package io.committed.ketos.common.graphql.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;

/** Collection of entities. */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Entities extends AbstractGraphQLNode {
  private Flux<BaleenEntity> results;

  private Mono<Long> total;

  private long offset;

  private long size;

  @Builder
  public Entities(
      final GraphQLNode parent,
      final Mono<Long> total,
      final Flux<BaleenEntity> results,
      final long offset,
      final long size) {
    super(parent);
    this.offset = offset;
    this.size = size;
    this.results = results.doOnNext(r -> r.setParent(this)).cache();
    this.total = total;
  }
}
