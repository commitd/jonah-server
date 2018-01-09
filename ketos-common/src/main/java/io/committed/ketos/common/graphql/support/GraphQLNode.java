package io.committed.ketos.common.graphql.support;

import java.util.Optional;
import io.leangen.graphql.annotations.GraphQLIgnore;
import lombok.Data;


@Data
@GraphQLIgnore
// TODO: Because SQPR seems to be ignoring @GraphQLIgnore, but as BeanResolver on it tends it pull
// out getters hence and that causes problems with this class (which is should have ignored).
// So I've renamed
public class GraphQLNode {

  private Object context;

  public <T> T getContext() {
    return (T) context;
  }

  public <T> T findRawParent() {
    if (hasParent()) {
      return ((AbstractGraphQLNodeSupport<?>) context).getGqlNode().getContext();
    } else {
      return null;
    }
  }

  public <T> Optional<T> findParent() {
    return Optional.ofNullable((T) findRawParent());
  }

  public boolean hasParent() {
    return context != null && context instanceof AbstractGraphQLNodeSupport
        && ((AbstractGraphQLNodeSupport<?>) context).getGqlNode() != null;
  }

  // This is checked
  @SuppressWarnings("unchecked")
  public <T> Optional<T> findParent(final Class<T> clazz) {
    if (context != null && clazz.isInstance(context)) {
      return Optional.of((T) context);
    }

    if (hasParent()) {
      final Object parent = findRawParent();
      if (clazz.isInstance(parent)) {
        return Optional.of((T) parent);
      } else if (parent instanceof GraphQLNode) {
        return ((GraphQLNode) parent).findParent(clazz);
      } else if (parent instanceof AbstractGraphQLNodeSupport) {
        return ((AbstractGraphQLNodeSupport<?>) parent).getGqlNode().findParent(clazz);
      }
    }

    return Optional.empty();

  }
}
