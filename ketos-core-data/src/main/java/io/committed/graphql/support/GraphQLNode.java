package io.committed.graphql.support;

import java.util.Optional;

import io.leangen.graphql.annotations.GraphQLIgnore;
import lombok.Data;

@GraphQLIgnore
@Data
public class GraphQLNode {

  private Object context;

  public <T> T getContext() {
    return (T) context;
  }

  public Object getRawParent() {
    if (hasParent()) {
      return Optional.of(((GraphQLNode) context).getContext());
    } else {
      return null;
    }
  }

  public <T> Optional<T> getParent() {
    return Optional.ofNullable((T) getRawParent());
  }

  public boolean hasParent() {
    return context != null
        && context instanceof GraphQLNode
        && ((GraphQLNode) context).getContext() != null;
  }

  // This is checked
  @SuppressWarnings("unchecked")
  public <T> Optional<T> findParent(final Class<T> clazz) {
    if (context != null && clazz.isInstance(context)) {
      return Optional.of((T) context);
    }

    if (hasParent()) {
      final Object parent = getRawParent();
      if (clazz.isInstance(parent)) {
        return Optional.of((T) parent);
      } else if (parent instanceof GraphQLNode) {
        return ((GraphQLNode) parent).findParent(clazz);
      }
    }

    return Optional.empty();

  }
}
