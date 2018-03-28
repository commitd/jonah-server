package io.committed.ketos.common.graphql.intermediate;

import io.committed.ketos.common.data.BaleenDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Results of a document search .
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentSearchResult {

  private Flux<BaleenDocument> results;

  private Mono<Long> total;

  private long offset;

  private long size;
}
