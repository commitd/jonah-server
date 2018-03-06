package io.committed.ketos.common.data;

import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
public class BaleenEntity extends AbstractGraphQLNode {

  @GraphQLId
  @GraphQLQuery(name = "id", description = "The id for this entity")
  private String id;

  @GraphQLQuery(name = "docId",
      description = "The document in which this entity is mentioned")
  private String docId;

  @GraphQLQuery(name = "type",
      description = "The type of this entity")
  private String type;

  @GraphQLQuery(name = "subType",
      description = "The subtype of this entity")
  private String subType;

  @GraphQLQuery(name = "value",
      description = "The value of this entity")
  private String value;

  @GraphQLQuery(name = "properties",
      description = "Additional properties of this entity")
  private PropertiesMap properties;

  public BaleenEntity(final String id, final String docId, final String type, final String subType,
      final String value, final PropertiesMap properties) {
    this.id = id;
    this.docId = docId;
    this.type = type;
    this.subType = subType;
    this.value = value;
    this.properties = properties == null ? new PropertiesMap() : properties;
  }


}
