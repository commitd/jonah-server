package io.committed.ketos.plugins.data.baleenmongo.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.committed.ketos.plugins.data.baleenmongo.dto.Relation;
import lombok.Data;

@Document(collection = "relations")
@Data
public class BaleenRelation {
  @Id
  private String id;

  private String externalId;

  private String docId;

  @Indexed
  private String source;

  @Indexed
  private String target;

  private int begin;

  private int end;

  private String type;

  private String relationshipType;

  private String relationSubtype;

  private String value;

  private double confidence;

  public Relation toRelation() {
    final Relation r = new Relation();
    r.setId(getExternalId());
    r.setDocId(getDocId());
    r.setBegin(getBegin());
    r.setEnd(getEnd());
    r.setType(getType());
    r.setRelationshipType(getRelationshipType());
    r.setRelationSubtype(getRelationSubtype());
    r.setValue(getValue());
    r.setConfidence(getConfidence());
    r.setSourceId(getSource());
    r.setTargetId(getTarget());
    return r;
  }

}
