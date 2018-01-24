package io.committed.ketos.common.graphql.input;

import lombok.Data;

@Data
public class RelationProbe {

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


}
