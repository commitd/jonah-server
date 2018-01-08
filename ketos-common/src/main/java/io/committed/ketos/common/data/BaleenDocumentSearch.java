package io.committed.ketos.common.data;

import io.committed.ketos.common.graphql.support.AbstractGraphQLNodeSupport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BaleenDocumentSearch extends AbstractGraphQLNodeSupport<BaleenDocumentSearch> {

  private String query;

  private int offset;

  private int size;
}
