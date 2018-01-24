package io.committed.ketos.common.graphql.input;

import io.committed.ketos.common.data.BaleenDocument;
import lombok.Data;
import reactor.core.publisher.Flux;

@Data
public class DocumentSearchResult {

  private Flux<BaleenDocument> results;

  private long total;
}
