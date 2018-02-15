package io.committed.ketos.common.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class MapUtils {

  private MapUtils() {
    // Singleton
  }

  public static Optional<Object> getAsKey(final Map<String, Object> metadata, final String key) {
    return Optional.ofNullable(metadata.get(key));
  }

  // This is checked by isInstance
  @SuppressWarnings("unchecked")
  public static <T> Optional<T> getAsKey(final Map<String, Object> metadata, final String key,
      final Class<? extends T> clazz) {
    return getAsKey(metadata, key)
        .flatMap(o -> {
          if (clazz.isInstance(o)) {
            return Optional.of((T) o);
          } else {
            return Optional.empty();
          }
        });
  }

  public static <T> T getAsKey(final Map<String, Object> metadata, final String key,
      final T defaultValue) {
    Objects.requireNonNull(defaultValue);
    final Class<T> clazz = (Class<T>) defaultValue.getClass();
    final Optional<T> optional = getAsKey(metadata, key, clazz);
    return optional.orElse(defaultValue);
  }

  public static Optional<String> getStringAsKey(final Map<String, Object> metadata, final String key) {
    return getAsKey(metadata, key, String.class)
        .filter(s -> !s.isEmpty());
  }

  public static Collection<String> getStringsAsKey(final Map<String, Object> metadata, final String key) {
    final Optional<Object> optional = getAsKey(metadata, key);

    // TODO: Should we toString things here..., should we filter out non strings?

    if (optional.isPresent()) {
      final Object o = optional.get();

      if (o instanceof Collection) {
        final Collection<Object> c = (Collection<Object>) o;
        if (c.stream().allMatch(String.class::isInstance)) {
          return (Collection<String>) o;
        }
      }
    }

    return Collections.emptyList();

  }

  public static Optional<Date> getDateAsKey(final Map<String, Object> properties, final String key) {
    return getAsKey(properties, key)
        .flatMap(o -> {
          Date d = null;
          if (o instanceof Date) {
            d = (Date) o;
          } else if (o instanceof Long || o instanceof Integer) {
            final long l = (long) o;
            d = new Date(l);
          }

          return Optional.ofNullable(d);
        });
  }
}
