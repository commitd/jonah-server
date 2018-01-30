package io.committed.ketos.plugins.data.mongo.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.graphql.input.RelationProbe;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "full_relations")
@Data
@NoArgsConstructor
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
        .sourceType(sourceType)
        .targetType(targetType)
        .sourceValue(sourceValue)
        .targetValue(targetValue)
        .build();
  }


  public MongoRelation(final RelationProbe probe) {
    this.begin = probe.getBegin();
    this.confidence = probe.getConfidence();
    this.docId = probe.getDocId();
    this.end = probe.getEnd();
    this.externalId = probe.getId();
    this.relationshipType = probe.getRelationshipType();
    this.relationSubtype = probe.getRelationSubtype();
    this.source = probe.getSourceId();
    this.sourceType = probe.getSourceType();
    this.sourceValue = probe.getSourceValue();
    this.target = probe.getTargetId();
    this.targetType = probe.getTargetType();
    this.targetValue = probe.getTargetType();
    this.type = probe.getType();
    this.value = probe.getValue();

  }

}
