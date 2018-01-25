package io.committed.ketos.plugins.data.mongo.utils;

import java.util.List;
import org.springframework.data.domain.ExampleMatcher;
import com.google.common.base.Joiner;

public class MongoUtils {
  private static final Joiner FIELD_JOINER = Joiner.on(".").skipNulls();


  public static String joinField(final List<String> path) {
    return FIELD_JOINER.join(path);
  }


  public static ExampleMatcher exampleMatcher() {
    // Spring by default adds in the _class in "MongoDocument" but since save the class via Spring
    // that field does not exist. So we ignore that field when creating the query.
    return ExampleMatcher.matching()
        .withIgnorePaths("_class");
  }
}
