package io.committed.ketos.common.data;

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
public class BaleenDocuments extends AbstractGraphQLNode {
  private final Flux<BaleenDocument> results;

  private final Mono<Long> totalCount;

  @Builder
  public BaleenDocuments(final GraphQLNode parent, final Mono<Long> totalCount, final Flux<BaleenDocument> results) {
    super(parent);
    this.results = results.doOnNext(r -> r.setParent(this)).cache();
    this.totalCount = totalCount;
  }
}
