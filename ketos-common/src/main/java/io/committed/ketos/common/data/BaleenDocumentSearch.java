package io.committed.ketos.common.data;

import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaleenDocumentSearch extends AbstractGraphQLNode {

  private final String query;

  private final int offset;

  private final int size;

  @Builder
  public BaleenDocumentSearch(final GraphQLNode parent, final String query, final int offset, final int size) {
    super(parent);
    this.query = query;
    this.offset = offset;
    this.size = size;
  }
}
