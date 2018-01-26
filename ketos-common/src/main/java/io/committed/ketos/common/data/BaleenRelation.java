package io.committed.ketos.common.data;

import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.leangen.graphql.annotations.GraphQLId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class BaleenRelation extends AbstractGraphQLNode {

  @GraphQLId
  private final String id;

  private final String docId;

  private final String sourceId;

  private final String targetId;

  private final String sourceType;

  private final String targetType;

  private String sourceValue;

  private String targetValue;

  private final int begin;

  private final int end;

  private final String type;

  private final String relationshipType;

  private final String relationSubtype;

  private final String value;

  private final double confidence;


}
