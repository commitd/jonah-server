package io.committed.ketos.common.data;

import java.util.Map;
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

  private final int begin;

  private final int end;

  private final String type;

  private final String subtype;

  private final String value;

  private final BaleenMention source;

  private final BaleenMention target;

  private final Map<String, Object> properties;

}
