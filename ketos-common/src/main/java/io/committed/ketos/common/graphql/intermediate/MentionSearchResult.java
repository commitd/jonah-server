package io.committed.ketos.common.graphql.intermediate;

import io.committed.ketos.common.data.BaleenMention;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@Builder
@AllArgsConstructor
public class MentionSearchResult {

  private Flux<BaleenMention> results;

  private Mono<Long> total;
}
