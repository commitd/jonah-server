package io.committed.ketos.data.jpa.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentInfo;
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
    return BaleenDocument.builder()
        .id(externalId)
        .content(content)
        .metadata(metadata.collectMap(JpaDocumentMetadata::getName, m -> (Object) m.getValue())
            .block())
        .info(BaleenDocumentInfo.builder()
            .caveats(caveats)
            .classification(classification)
            .releasability(releasability)
            .language(language)
            .source(source)
            .timestamp(processed)
            .type(type)
            .build())
        .build();
  }
}