package io.committed.ketos.common.graphql.output;

import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Relations extends AbstractGraphQLNode {
  private final Flux<BaleenRelation> results;

  private final Mono<Long> total;

  @Builder
  public Relations(final GraphQLNode parent, final Mono<Long> total, final Flux<BaleenRelation> results) {
    super(parent);
    this.results = results.doOnNext(r -> r.setParent(this)).cache();
    this.total = total;
  }
}