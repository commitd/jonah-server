package io.committed.ketos.data.jpa.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentMetadata;
import io.committed.ketos.common.graphql.input.DocumentProbe;
import lombok.Data;
import reactor.core.publisher.Flux;

@Entity
@Data
public class JpaDocument {

  @Id
  private Long key;

  private String externalId;
  private String type;
  private String source;
  private String content;
  private String language;
  private Date processed;
  private String classification;
  private List<String> caveats;
  private List<String> releasability;

  public BaleenDocument toBaleenDocument(final Flux<JpaDocumentMetadata> metadata) {

    final Map<String, Object> properties = new HashMap<>();

    properties.put(BaleenProperties.DOCUMENT_TYPE, type);
    properties.put(BaleenProperties.SOURCE, source);
    properties.put(BaleenProperties.LANGUAGE, language);
    properties.put(BaleenProperties.DOCUMENT_DATE, processed);
    properties.put(BaleenProperties.TIMESTAMP, processed);
    properties.put(BaleenProperties.CLASSIFICATION, classification);
    properties.put(BaleenProperties.CAVEATS, caveats);
    properties.put(BaleenProperties.RELEASABILITY, releasability);

    return BaleenDocument.builder()
        .id(externalId)
        .content(content)
        .metadata(
            metadata.map(m -> new BaleenDocumentMetadata(m.getName(), m.getValue())))
        .properties(properties)
        .build();
  }

  public JpaDocument(final DocumentProbe probe) {
    this.externalId = probe.getId();
    this.type = probe.getInfo().getType();
    this.source = probe.getInfo().getSource();
    this.content = probe.getContent();
    this.language = probe.getInfo().getLanguage();
    this.processed = probe.getInfo().getTimestamp();
    this.classification = probe.getInfo().getClassification();

    // NOTE:
    // https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example.execution
    // Only single item properties can be used for matching at the moment in JPA
    // thus cevets and releasibility are ignored

  }
}
