package io.committed.ketos.common.graphql.input;

import io.committed.invest.core.dto.collections.PropertiesMap;
import lombok.Data;

/**
 * Search by example mention.
 */
@Data
public class MentionProbe {

  private String docId;
  private String id;
  private Integer begin;
  private Integer end;
  private String type;
  private String subType;
  private String value;
  private String entityId;
  private PropertiesMap properties;

  public MentionFilter toFilter() {
    final MentionFilter filter = new MentionFilter();

    filter.setDocId(docId);
    filter.setId(id);
    filter.setType(type);
    filter.setSubType(subType);
    filter.setValue(value);
    filter.setProperties(properties);
    filter.setEntityId(entityId);

    return filter;

  }


}
