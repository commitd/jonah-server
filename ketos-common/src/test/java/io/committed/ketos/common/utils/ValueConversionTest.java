package io.committed.ketos.common.utils;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import io.committed.ketos.common.utils.ValueConversion;

public class ValueConversionTest {

  @Test
  public void testHasStringValue() {
    assertThat(ValueConversion.hasStringValue("real thing")).isTrue();
    assertThat(ValueConversion.hasStringValue("thing")).isTrue();
    assertThat(ValueConversion.hasStringValue("**")).isTrue();

    assertThat(ValueConversion.hasStringValue(null)).isFalse();
    assertThat(ValueConversion.hasStringValue("")).isFalse();
    assertThat(ValueConversion.hasStringValue("*")).isFalse();
    assertThat(ValueConversion.hasStringValue("  *")).isFalse();
    assertThat(ValueConversion.hasStringValue("  ")).isFalse();

  }

  @Test
  public void testStringValue() {
    assertThat(ValueConversion.stringValue("real thing").get()).isEqualTo("real thing");
    assertThat(ValueConversion.stringValue("real").get()).isEqualTo("real");

    assertThat(ValueConversion.stringValue("*")).isEmpty();
    assertThat(ValueConversion.stringValue("   ")).isEmpty();


  }

  @Test
  public void testIsValueOrOther() {
    assertThat(ValueConversion.isValueOrOther("real thing")).isTrue();
    assertThat(ValueConversion.isValueOrOther("*")).isFalse();


    final Object object = new Object();
    assertThat(ValueConversion.isValueOrOther(object)).isTrue();
    assertThat(ValueConversion.isValueOrOther(null)).isFalse();

  }

  @Test
  public void testValueOrNull() {
    assertThat(ValueConversion.valueOrNull("real thing")).isEqualTo("real thing");
    assertThat(ValueConversion.valueOrNull("*")).isNull();
    assertThat(ValueConversion.valueOrNull("")).isNull();


    final Object object = new Object();
    assertThat(ValueConversion.valueOrNull(object)).isSameAs(object);
    assertThat(ValueConversion.valueOrNull(null)).isNull();
  }
}
