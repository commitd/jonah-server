package io.committed.ketos.common.graphql.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public abstract class AbstractGraphQLNodeSupport<T extends AbstractGraphQLNodeSupport<T>> {

  @JsonIgnore
  private GraphQLNode gqlNode;

  @JsonIgnore
  public GraphQLNode getOrCreateGqlNode() {
    GraphQLNode support = getGqlNode();
    if (support == null) {
      support = new GraphQLNode();
      setGqlNode(support);
    }
    return support;
  }

  @JsonIgnore
  public T addNodeContext(final Object context) {
    getOrCreateGqlNode().setContext(context);
    return (T) this;
  }
}
