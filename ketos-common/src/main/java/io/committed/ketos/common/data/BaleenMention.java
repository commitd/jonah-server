package io.committed.ketos.common.data;

import java.util.Collections;
import java.util.Map;
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
public class BaleenMention extends AbstractGraphQLNodeSupport<BaleenMention> {



  @GraphQLId
  private String id;
  private double confidence;
  private int begin;
  private int end;
  private String type;
  private String value;
  private String entityId;


  @Builder.Default
  private Map<String, Object> properties = Collections.emptyMap();
}
