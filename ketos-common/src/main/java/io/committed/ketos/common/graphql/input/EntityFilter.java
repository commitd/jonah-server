package io.committed.ketos.common.graphql.input;

import java.util.Map;
import lombok.Data;

@Data
public class EntityFilter {

  private String docId;

  private String id;
  private String entityId;

  private String type;
  private String value;

  private Map<String, Object> properties;


}
