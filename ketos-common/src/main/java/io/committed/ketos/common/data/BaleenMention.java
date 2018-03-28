package io.committed.ketos.common.data;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.leangen.graphql.annotations.GraphQLId;

/** GraphQL/DTO representation of a Baleen document */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
public class BaleenMention extends AbstractGraphQLNode {

  @GraphQLId private String id;
  private int begin;
  private int end;
  private String type;
  private String subType;
  private String value;
  private String entityId;
  private String docId;

  private PropertiesMap properties;

  public BaleenMention(
      @GraphQLId final String id,
      final int begin,
      final int end,
      final String type,
      final String subType,
      final String value,
      final String entityId,
      final String docId,
      final PropertiesMap properties) {
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
