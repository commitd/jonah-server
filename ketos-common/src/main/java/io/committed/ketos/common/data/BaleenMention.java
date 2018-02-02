package io.committed.ketos.common.data;

import java.util.Collections;
import java.util.Map;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.leangen.graphql.annotations.GraphQLId;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class BaleenMention extends AbstractGraphQLNode {

  @GraphQLId
  private final String id;
  private final double confidence;
  private final int begin;
  private final int end;
  private final String type;
  private final String value;
  private final String entityId;
  private final String docId;
  private final Map<String, Object> properties;

  public BaleenMention(@GraphQLId final String id, final double confidence, final int begin, final int end,
      final String type, final String value,
      final String entityId, final String docId, final Map<String, Object> properties) {
    super();
    this.id = id;
    this.confidence = confidence;
    this.begin = begin;
    this.end = end;
    this.type = type;
    this.value = value;
    this.entityId = entityId;
    this.docId = docId;
    this.properties = properties == null ? Collections.emptyMap() : properties;
  }


}
