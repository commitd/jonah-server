package io.committed.ketos.common.graphql.output;

import io.committed.ketos.common.data.BaleenDocument;
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
public class Documents extends AbstractGraphQLNode {
  private Flux<BaleenDocument> results;

  private Mono<Long> total;

  private long offset;

  private long size;

  @Builder
  public Documents(final GraphQLNode parent, final Mono<Long> total, final Flux<BaleenDocument> results,
      final long offset, final long size) {
    super(parent);
    this.offset = offset;
    this.size = size;
    this.results = results == null ? Flux.empty() : results.doOnNext(r -> r.setParent(this)).cache();
    this.total = total == null ? Mono.empty() : total;
  }
}
