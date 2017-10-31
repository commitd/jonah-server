package io.committed.ketos.common.data;

import io.committed.ketos.common.graphql.support.AbstractGraphQLNodeSupport;
import io.leangen.graphql.annotations.GraphQLId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class BaleenRelation extends AbstractGraphQLNodeSupport<BaleenRelation> {

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
