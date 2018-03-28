package io.committed.ketos.common.utils;

import java.util.Optional;

/**
 * Helper to establish some convensions for input values.
 *
 * <p>For example a search for * would return all for ES but documents containing a * in Mongo.
 */
public final class ValueConversion {

  private ValueConversion() {
    // Singleton
  }

  /**
   * Allow use of "*" for any match in Mongo (like ES).
   *
   * @return the string
   */
  public static Optional<String> stringValue(final String value) {
    if (!hasStringValue(value)) {
      return Optional.empty();
    } else {
      return Optional.of(value);
    }
  }

  /**
   * check if there's a queryable string in the value.
   *
   * @param value the value
   * @return true, if successful
   */
  public static boolean hasStringValue(final String value) {
    if (value == null) return false;
    final String s = value.trim();
    if (s.isEmpty()) return false;
    return !"*".equals(s);
  }

  /**
   * Checks if is actual (string) value or some other object.
   *
   * @param o the o
   * @return true, if is value or other
   */
  public static boolean isValueOrOther(final Object o) {
    if (o == null) {
      return false;
    }
    return !(o instanceof String) || ValueConversion.hasStringValue((String) o);
  }

  /**
   * Get the santised value or null.
   *
   * @param o the o
   * @return the object
   */
  public static Object valueOrNull(final Object o) {
    if (o == null) {
      return null;
    }

    if (!(o instanceof String)) {
      return o;
    }

    return stringValue((String) o).orElse(null);
  }
}
