package io.committed.ketos.common.baleenconsumer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

public class ElasticsearchMappingTest {

  @Test
  public void testToAggregationPath() {
    assertThat(ElasticsearchMapping.toAggregationField(Arrays.asList("metadata", "key")))
        .isEqualTo("metadata.key");

    assertThat(ElasticsearchMapping.toAggregationField(Arrays.asList("metadata", "value")))
        .isEqualTo("metadata.value.keyword");

    assertThat(ElasticsearchMapping.toAggregationField(Arrays.asList("properties", "something")))
        .isEqualTo("properties.something.keyword");

    assertThat(ElasticsearchMapping.toAggregationField(Arrays.asList("properties", "geoJson")))
        .isEqualTo("properties.geoJson");
  }
}
