package io.committed.ketos.common.graphql.input;

import lombok.Data;

import io.committed.invest.core.dto.collections.PropertiesMap;

/** Search by example relation. */
@Data
public class RelationProbe {

  private String id;

  private String docId;

  private Integer begin;

  private Integer end;

  private String type;

  private String subType;

  private String value;

  private MentionProbe source;

  private MentionProbe target;

  private PropertiesMap properties;

  public RelationFilter toFilter() {
    final RelationFilter filter = new RelationFilter();

    filter.setDocId(docId);
    filter.setId(id);
    filter.setSubType(subType);
    filter.setType(type);
    filter.setValue(value);
    filter.setProperties(properties);

    if (source != null) {
      filter.setSource(source.toFilter());
    }

    if (target != null) {
      filter.setTarget(target.toFilter());
    }

    return filter;
  }
}
