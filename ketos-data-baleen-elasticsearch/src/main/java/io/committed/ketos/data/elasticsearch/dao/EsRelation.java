package io.committed.ketos.data.elasticsearch.dao;

import org.springframework.data.annotation.Id;

import io.committed.ketos.common.data.BaleenRelation;
import lombok.Data;


@Data
public class EsRelation {
  @Id
  private String id;

  private String externalId;

  private String docId;

  private String source;

  private String target;

  private int begin;

  private int end;

  private String type;

  private String relationshipType;

  private String relationSubtype;

  private String value;

  private double confidence;

  // Used to carry through flux, etc
  private transient String documentId;

  public BaleenRelation toBaleenRelation() {
    return BaleenRelation.builder()
        .begin(begin)
        .confidence(confidence)
        .docId(documentId)
        .end(end)
        .id(externalId)
        .relationshipType(relationshipType)
        .relationSubtype(relationSubtype)
        .sourceId(source)
        .targetId(target)
        .type(type)
        .value(value)
        .build();
  }
}
