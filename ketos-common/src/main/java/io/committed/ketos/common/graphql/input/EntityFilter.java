package io.committed.ketos.common.graphql.input;

import lombok.Data;

@Data
public class EntityFilter {

  private String docId;
  private String id;

  // TODO: In future if we have a real Baleen Entity
  // private String type;
  // private String value;
  // private Map<String, Object> properties;

}
