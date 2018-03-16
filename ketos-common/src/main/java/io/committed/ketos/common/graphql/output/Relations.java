package io.committed.ketos.common.graphql.output;

import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Relations extends AbstractGraphQLNode {
  private Flux<BaleenRelation> results;

  private Mono<Long> total;

  private long offset;

  private long size;

  @Builder
  public Relations(final GraphQLNode parent, final Mono<Long> total, final Flux<BaleenRelation> results,
      final long offset, final long size) {
    super(parent);
    this.offset = offset;
    this.size = size;
    this.results = results.doOnNext(r -> r.setParent(this)).cache();
    this.total = total;
  }
}
