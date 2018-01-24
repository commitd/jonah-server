package io.committed.ketos.common.graphql.support;

import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
