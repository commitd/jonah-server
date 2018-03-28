package io.committed.ketos.common.graphql.input;

import lombok.Data;

import io.committed.invest.core.dto.collections.PropertiesMap;

/** Search by example entity. */
@Data
public class EntityProbe {

  private String id;
  private String docId;
  private String type;
  private String subType;
  private String value;
  private PropertiesMap properties;

  public EntityFilter toFilter() {
    final EntityFilter filter = new EntityFilter();

    filter.setDocId(docId);
    filter.setId(id);
    filter.setProperties(properties);
    filter.setSubType(subType);
    filter.setType(type);
    filter.setValue(value);

    return filter;
  }
}
