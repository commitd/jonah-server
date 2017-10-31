package io.committed.ketos.common.data;

import io.committed.ketos.common.graphql.support.AbstractGraphQLNodeSupport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaleenDocuments extends AbstractGraphQLNodeSupport<BaleenDocuments> {
  private Flux<BaleenDocument> results;
}
