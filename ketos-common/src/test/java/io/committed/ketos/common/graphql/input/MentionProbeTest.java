package io.committed.ketos.common.graphql.input;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import io.committed.invest.core.dto.collections.PropertiesMap;

public class MentionProbeTest {

  @Test
  public void test() {

    final MentionProbe probe = new MentionProbe();
    probe.setDocId("docId");
    probe.setId("id");
    probe.setProperties(new PropertiesMap());
    probe.setSubType("subType");
    probe.setType("type");
    probe.setValue("value");

    final MentionFilter filter = probe.toFilter();

    assertThat(filter.getDocId()).isEqualTo(probe.getDocId());
    assertThat(filter.getId()).isEqualTo(probe.getId());
    assertThat(filter.getProperties()).isSameAs(probe.getProperties());
    assertThat(filter.getSubType()).isEqualTo(probe.getSubType());
    assertThat(filter.getType()).isEqualTo(probe.getType());
    assertThat(filter.getValue()).isEqualTo(probe.getValue());
  }
}
