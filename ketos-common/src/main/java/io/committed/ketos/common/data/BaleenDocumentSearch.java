package io.committed.ketos.common.data;

import io.committed.ketos.common.graphql.support.AbstractGraphQLNodeSupport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaleenDocumentSearch extends AbstractGraphQLNodeSupport<BaleenDocumentSearch> {

  private String query;

  private int limit;
}
