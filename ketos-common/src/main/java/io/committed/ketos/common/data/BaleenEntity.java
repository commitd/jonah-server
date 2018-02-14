package io.committed.ketos.common.data;

import java.util.Collections;
import java.util.Map;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class BaleenEntity extends AbstractGraphQLNode {

  @GraphQLId
  @GraphQLQuery(name = "id", description = "The id for this entity")
  private final String id;

  @GraphQLQuery(name = "docId",
      description = "The document in which this entity is mentioned")
  private final String docId;

  @GraphQLQuery(name = "type",
      description = "The type of this entity")
  private final String type;

  @GraphQLQuery(name = "subType",
      description = "The subtype of this entity")
  private final String subType;

  @GraphQLQuery(name = "value",
      description = "The value of this entity")
  private final String value;

  @GraphQLQuery(name = "properties",
      description = "Additional properties of this entity")
  private final Map<String, Object> properties;

  public BaleenEntity(final String id, final String docId, final String type, final String subType,
      final String value, final Map<String, Object> properties) {
    this.id = id;
    this.docId = docId;
    this.type = type;
    this.subType = subType;
    this.value = value;
    this.properties = properties == null ? Collections.emptyMap() : properties;
  }


}
