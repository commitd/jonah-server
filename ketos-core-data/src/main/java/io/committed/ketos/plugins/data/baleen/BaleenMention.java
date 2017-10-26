package io.committed.ketos.plugins.data.baleen;

import io.committed.graphql.support.AbstractGraphQLNodeSupport;
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
public class BaleenMention extends AbstractGraphQLNodeSupport<BaleenMention> {

  @GraphQLId
  private String id;
  private double confidence;
  private int begin;
  private int end;
  private String type;
  private String value;
  private String entityId;


}
