package io.committed.ketos.common.graphql.input;

import lombok.Data;

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

  public RelationFilter toFilter() {
    final RelationFilter filter = new RelationFilter();

    filter.setDocId(docId);
    filter.setId(id);
    filter.setSubType(subType);
    filter.setType(type);
    filter.setValue(value);

    if (source != null) {
      filter.setSource(source.toFilter());
    }

    if (target != null) {
      filter.setTarget(target.toFilter());
    }

    return filter;
  }


}
