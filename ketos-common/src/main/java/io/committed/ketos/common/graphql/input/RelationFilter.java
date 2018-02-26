package io.committed.ketos.common.graphql.input;

import io.committed.invest.core.dto.collections.PropertiesMap;
import lombok.Data;

@Data
public class RelationFilter {
  private String id;

  private String docId;

  private String type;

  private String subType;

  private String value;

  private PropertiesMap properties;

  private MentionFilter source;

  private MentionFilter target;

}
