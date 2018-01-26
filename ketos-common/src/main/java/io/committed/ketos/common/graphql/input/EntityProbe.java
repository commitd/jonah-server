package io.committed.ketos.common.graphql.input;

import lombok.Data;

@Data
public class EntityProbe {

  private String id;
  private String docId;

  // TODO: In future (if entities are output by Baleen we should include these). This would involve
  // new BaleenConsumer (akin to the full_relation collection)
  // private String type;
  // private String value;
  // private Map<String, Object> properties;


}
