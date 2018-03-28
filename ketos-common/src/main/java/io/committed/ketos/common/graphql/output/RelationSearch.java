package io.committed.ketos.common.graphql.output;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;

/** A full relation search query. */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RelationSearch extends AbstractGraphQLNode {

  private RelationFilter relationFilter;

  @Builder
  public RelationSearch(final GraphQLNode parent, final RelationFilter relationFilter) {
    super(parent);
    this.relationFilter = relationFilter;
  }
}
