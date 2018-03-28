package io.committed.ketos.common.graphql.input;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import io.committed.invest.core.dto.collections.PropertiesList;
import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.ketos.common.graphql.input.DocumentProbe.DocumentInfoProbe;

public class DocumentProbeTest {

  @Test
  public void testToDocumentFilter() {
    final DocumentProbe probe = new DocumentProbe();
    probe.setContent("content");
    probe.setId("id");
    probe.setMetadata(new PropertiesList());
    probe.setProperties(new PropertiesMap());

    final DocumentInfoProbe documentInfoProbe = new DocumentInfoProbe();
    documentInfoProbe.setCaveats("caveats");
    documentInfoProbe.setLanguage("lang");
    documentInfoProbe.setSource("source");
    documentInfoProbe.setClassification("class");

    probe.setInfo(documentInfoProbe);

    final DocumentFilter filter = probe.toDocumentFilter();
    ;

    assertThat(filter.getContent()).isEqualTo(probe.getContent());
    assertThat(filter.getId()).isEqualTo(probe.getId());
    assertThat(filter.getMetadata()).isSameAs(probe.getMetadata());
    assertThat(filter.getProperties()).isSameAs(probe.getProperties());

    assertThat(filter.getInfo().getCaveats()).isEqualTo(documentInfoProbe.getCaveats());
    assertThat(filter.getInfo().getClassification())
        .isEqualTo(documentInfoProbe.getClassification());
    assertThat(filter.getInfo().getSource()).isEqualTo(documentInfoProbe.getSource());
    assertThat(filter.getInfo().getLanguage()).isEqualTo(documentInfoProbe.getLanguage());
  }
}
