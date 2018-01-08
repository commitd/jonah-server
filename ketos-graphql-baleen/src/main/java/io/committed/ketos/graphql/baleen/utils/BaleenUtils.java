package io.committed.ketos.graphql.baleen.utils;

import java.util.Map;
import org.springframework.util.StringUtils;

public final class BaleenUtils {

  private BaleenUtils() {
    // Singleton
  }

  public static String getAsMetadataKey(final Map<String, Object> metadata, final String key) {
    final Object v = metadata.get(key);
    if (v != null && v instanceof String && !StringUtils.isEmpty(v)) {
      return (String) v;
    } else {
      return null;
    }
  }
}
