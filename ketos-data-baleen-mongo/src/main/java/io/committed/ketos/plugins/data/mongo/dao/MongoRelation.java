package io.committed.ketos.plugins.data.mongo.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import io.committed.ketos.common.data.BaleenRelation;
import lombok.Data;

@Document(collection = "full_relations")
@Data
public class MongoRelation {
  @Id
  private String id;

  private String externalId;

  private String docId;

  @Indexed
  private String source;

  @Indexed
  private String target;

  private Integer begin;

  private Integer end;

  private String type;

  private String relationshipType;

  private String relationSubtype;

  private String value;

  private Double confidence;


  private String targetValue;
  private String targetType;

  private String sourceValue;
  private String sourceType;


  public BaleenRelation toRelation() {
    return BaleenRelation.builder()
        .id(getExternalId())
        .docId(getDocId())
        .begin(getBegin())
        .end(getEnd())
        .type(getType())
        .relationshipType(getRelationshipType())
        .relationSubtype(getRelationSubtype())
        .value(getValue())
        .confidence(getConfidence())
        .sourceId(getSource())
        .targetId(getTarget())
        .build();
  }

}
