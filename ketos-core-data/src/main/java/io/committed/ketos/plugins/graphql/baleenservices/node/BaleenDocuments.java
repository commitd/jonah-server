package io.committed.ketos.plugins.graphql.baleenservices.node;

import io.committed.graphql.support.AbstractGraphQLNodeSupport;
import io.committed.ketos.plugins.data.baleen.BaleenDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaleenDocuments extends AbstractGraphQLNodeSupport<BaleenDocuments> {
  private Flux<BaleenDocument> documents;
}
