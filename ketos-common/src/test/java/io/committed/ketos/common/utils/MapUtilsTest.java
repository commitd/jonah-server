package io.committed.ketos.common.utils;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class MapUtilsTest {

  @Test
  public void testGetWithDefault() {
    final Map<String, Object> o = new HashMap<>();
    o.put("a", "b");

    assertThat(MapUtils.getAsKey(o, "a", "c")).isEqualTo("b");
    assertThat(MapUtils.getAsKey(o, "x", "c")).isEqualTo("c");
    assertThat(MapUtils.getAsKey(o, "a", 1)).isEqualTo(1);

  }

  @Test
  public void testGetStringAsKey() {
    final Map<String, Object> o = new HashMap<>();
    o.put("a", "b");
    o.put("n", 4);

    assertThat(MapUtils.getStringAsKey(o, "a").get()).isEqualTo("b");
    assertThat(MapUtils.getStringAsKey(o, "x")).isEmpty();
    assertThat(MapUtils.getStringAsKey(o, "n")).isEmpty();

  }

  @Test
  public void testGetStringsAsKey() {
    final Map<String, Object> o = new HashMap<>();
    o.put("a", "b");
    o.put("c", Arrays.asList("1", "2"));
    o.put("h", Arrays.asList("1", 2));

    o.put("n", 4);

    assertThat(MapUtils.getStringsAsKey(o, "x")).isEmpty();
    assertThat(MapUtils.getStringsAsKey(o, "a")).isEmpty();
    assertThat(MapUtils.getStringsAsKey(o, "n")).isEmpty();
    assertThat(MapUtils.getStringsAsKey(o, "c")).hasSize(2);
    assertThat(MapUtils.getStringsAsKey(o, "h")).isEmpty();

  }

  @Test
  public void testGetDateAsKey() {
    final Map<String, Object> o = new HashMap<>();
    o.put("a", "b");
    o.put("n", 4);
    o.put("d", new Date());

    assertThat(MapUtils.getDateAsKey(o, "a")).isEmpty();
    assertThat(MapUtils.getDateAsKey(o, "x")).isEmpty();
    assertThat(MapUtils.getDateAsKey(o, "n")).isPresent();
    assertThat(MapUtils.getDateAsKey(o, "d")).isPresent();

  }

}
