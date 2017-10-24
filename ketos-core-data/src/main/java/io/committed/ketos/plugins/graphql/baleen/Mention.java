package io.committed.ketos.plugins.graphql.baleen;

import io.leangen.graphql.annotations.GraphQLId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mention {

  @GraphQLId
  private String id;
  private double confidence;
  private int begin;
  private int end;
  private String type;
  private String value;
  private String entityId;


}
