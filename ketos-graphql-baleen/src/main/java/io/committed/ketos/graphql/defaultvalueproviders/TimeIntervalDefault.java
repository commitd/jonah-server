package io.committed.ketos.graphql.defaultvalueproviders;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Parameter;
import io.committed.invest.core.dto.constants.TimeInterval;
import io.leangen.graphql.generator.mapping.strategy.DefaultValueProvider;
import io.leangen.graphql.metadata.OperationArgumentDefaultValue;

public class TimeIntervalDefault implements DefaultValueProvider {


  @Override
  public OperationArgumentDefaultValue getDefaultValue(final Parameter parameter, final AnnotatedType parameterType,
      final OperationArgumentDefaultValue initialValue) {
    return new OperationArgumentDefaultValue(TimeInterval.MONTH);
  }

}
