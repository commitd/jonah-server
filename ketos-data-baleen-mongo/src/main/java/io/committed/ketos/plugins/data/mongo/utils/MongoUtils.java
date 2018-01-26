package io.committed.ketos.plugins.data.mongo.utils;

import java.util.List;
import com.google.common.base.Joiner;

public class MongoUtils {
  private static final Joiner FIELD_JOINER = Joiner.on(".").skipNulls();


  public static String joinField(final List<String> path) {
    return FIELD_JOINER.join(path);
  }
}
