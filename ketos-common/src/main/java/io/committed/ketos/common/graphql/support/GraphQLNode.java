package io.committed.ketos.common.graphql.support;

import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.leangen.graphql.annotations.GraphQLContext;

/**
 * A node in the response from GraphQL.
 *
 * Use this to provide access downstream (eg through {@link GraphQLContext} to information upstream.
 *
 * For example if you have a Grandparent node, which then have a Parent and that has Child, then the
 * child can request the Grandparent.class via findParent and will have access to the information
 * there. This allows the child to tailor its function to the 'contextual hierarchy' it is returning
 * within.
 *
 */
public interface GraphQLNode {

  @JsonIgnore
  GraphQLNode getParent();

  default boolean hasParent() {
    return getParent() != null;
  }

  default <P extends GraphQLNode> Optional<P> findParent(final Class<P> clazz) {
    // Are we that class?
    if (clazz.isInstance(this)) {
      return Optional.of(clazz.cast(this));
    }

    // No... recurse through parents
    final GraphQLNode parent = getParent();
    if (parent == null) {
      return Optional.empty();
    } else {
      return parent.findParent(clazz);
    }
  }

}
