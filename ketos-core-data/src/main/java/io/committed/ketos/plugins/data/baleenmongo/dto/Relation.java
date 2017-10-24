package io.committed.ketos.plugins.data.baleenmongo.dto;

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


}
