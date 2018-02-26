package io.committed.ketos.common.data;

import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.leangen.graphql.annotations.GraphQLId;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class BaleenRelation extends AbstractGraphQLNode {

  @GraphQLId
  private final String id;

  private final String docId;

  private final int begin;

  private final int end;

  private final String type;

  private final String subType;

  private final String value;

  private final BaleenMention source;

  private final BaleenMention target;

  private final PropertiesMap properties;

  public BaleenRelation(final String id, final String docId, final int begin, final int end,
      final String type, final String subtype,
      final String value, final BaleenMention source, final BaleenMention target,
      final PropertiesMap properties) {
    super();
    this.id = id;
    this.docId = docId;
    this.begin = begin;
    this.end = end;
    this.type = type;
    this.subType = subtype;
    this.value = value;
    this.source = source;
    this.target = target;
    this.properties = properties == null ? new PropertiesMap() : properties;

    // Ensure the correctness of the source and target

    if (this.source != null) {
      this.source.setParent(this);
    }
    if (this.target != null) {
      this.target.setParent(this);
    }
  }

}
