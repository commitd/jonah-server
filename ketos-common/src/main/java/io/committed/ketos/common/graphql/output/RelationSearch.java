package io.committed.ketos.common.graphql.output;

import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RelationSearch extends AbstractGraphQLNode {

  private final RelationFilter relationFilter;

  @Builder
  public RelationSearch(final GraphQLNode parent, final RelationFilter relationFilter) {
    super(parent);
    this.relationFilter = relationFilter;
  }
}
