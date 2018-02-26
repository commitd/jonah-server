package io.committed.ketos.common.data;

import io.committed.invest.core.dto.collections.PropertiesMap;
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
  private final int begin;
  private final int end;
  private final String type;
  private final String subType;
  private final String value;
  private final String entityId;
  private final String docId;

  private final PropertiesMap properties;

  public BaleenMention(@GraphQLId final String id, final int begin, final int end,
      final String type, final String subType, final String value,
      final String entityId, final String docId, final PropertiesMap properties) {
    super();
    this.id = id;
    this.begin = begin;
    this.end = end;
    this.type = type;
    this.subType = subType;
    this.value = value;
    this.entityId = entityId;
    this.docId = docId;
    this.properties = properties == null ? new PropertiesMap() : properties;
  }


}
