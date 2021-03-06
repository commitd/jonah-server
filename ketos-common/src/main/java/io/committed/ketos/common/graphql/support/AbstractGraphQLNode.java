package io.committed.ketos.common.graphql.support;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** Simple of GraphQLNode using parent hierarchy */
@Data
public abstract class AbstractGraphQLNode implements GraphQLNode {

  private GraphQLNode parent;

  protected AbstractGraphQLNode() {
    this(null);
  }

  public AbstractGraphQLNode(final GraphQLNode parent) {
    this.parent = parent;
  }

  @Override
  @JsonIgnore
  public GraphQLNode getParent() {
    return parent;
  }
}
