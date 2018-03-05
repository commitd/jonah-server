package io.committed.ketos.common.graphql.output;

import io.committed.ketos.common.data.BaleenMention;
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
public class Mentions extends AbstractGraphQLNode {
  private final Flux<BaleenMention> results;

  private final Mono<Long> total;

  private final long offset;

  private final long size;

  @Builder
  public Mentions(final GraphQLNode parent, final Mono<Long> total, final Flux<BaleenMention> results,
      final long offset, final long size) {
    super(parent);
    this.offset = offset;
    this.size = size;
    this.results = results.doOnNext(r -> r.setParent(this)).cache();
    this.total = total;
  }
}
