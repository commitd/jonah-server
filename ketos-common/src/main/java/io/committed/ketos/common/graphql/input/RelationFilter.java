package io.committed.ketos.common.graphql.input;

import lombok.Data;

@Data
public class RelationFilter {
  private String docId;

  private String sourceId;

  private String targetId;

  private String type;

  private String relationshipType;

  private String relationSubtype;

  private String value;

  // NOTE: Currently this isn't much better than a query by example, I'm not sure it ever will be but
  // it serves a different purpose

  private String sourceType;
  private String targetType;
  private String sourceValue;
  private String targetValue;

}
