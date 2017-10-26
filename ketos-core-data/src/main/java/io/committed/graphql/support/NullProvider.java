package io.committed.graphql.support;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Parameter;

import io.leangen.graphql.generator.mapping.strategy.DefaultValueProvider;
import io.leangen.graphql.metadata.OperationArgumentDefaultValue;

public class NullProvider implements DefaultValueProvider {

  @Override
  public OperationArgumentDefaultValue getDefaultValue(final Parameter parameter,
      final AnnotatedType parameterType, final OperationArgumentDefaultValue initialValue) {
    return null;
  }

}
