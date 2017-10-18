package io.committed.ketos.plugins.data.baleenmongo.dto;

import io.committed.ketos.plugins.data.baleenmongo.dao.BaleenRelation;
import io.leangen.graphql.annotations.GraphQLId;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Relation {

  @GraphQLId
  private String id;

  private String docId;

  private String sourceId;

  private String targetId;

  private int begin;

  private int end;

  private String type;

  private String relationshipType;

  private String relationSubtype;

  private String value;

  private double confidence;

  public Relation(final BaleenRelation baleen) {
    id = baleen.getExternalId();
    docId = baleen.getDocId();
    begin = baleen.getBegin();
    end = baleen.getEnd();
    type = baleen.getType();
    relationshipType = baleen.getRelationshipType();
    relationSubtype = baleen.getRelationSubtype();
    value = baleen.getValue();
    confidence = baleen.getConfidence();
    sourceId = baleen.getSource();
    targetId = baleen.getTarget();
  }

}
