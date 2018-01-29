package io.committed.ketos.common.graphql.input;

import java.util.Map;
import lombok.Data;

@Data
public class MentionProbe {

  private String docId;
  private String id;
  private double confidence;
  private int begin;
  private int end;
  private String type;
  private String value;
  private String entityId;
  private Map<String, Object> properties;

  public MentionFilter toFilter() {
    final MentionFilter filter = new MentionFilter();

    filter.setDocId(docId);
    filter.setId(id);
    filter.setType(type);
    filter.setValue(value);
    filter.setProperties(properties);
    filter.setEntityId(entityId);

    // TODO have this as after / before / below / above
    // filter.setConfidence;
    // filter.setBeing
    // filter.setEnd(end);

    return filter;

  }


}
