package io.committed.ketos.graphql.baleen.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;
import com.google.common.base.Splitter;

public final class BaleenUtils {

  // Fixed limit here to avoid some crazy depth, when in reality it'll be 2.
  private static final Splitter FIELD_SPLITTER = Splitter.on(".").trimResults().omitEmptyStrings().limit(5);

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

  public static List<String> fieldSplitter(final String field) {
    if (field == null) {
      return Collections.emptyList();
    }
    return FIELD_SPLITTER.splitToList(field);
  }
}
