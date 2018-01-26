package io.committed.ketos.common.graphql.input;

import java.util.Map;
import lombok.Data;

@Data
public class EntityProbe {

  private String id;
  private String docId;

  private String type;
  private String value;
  private Map<String, Object> properties;


}
