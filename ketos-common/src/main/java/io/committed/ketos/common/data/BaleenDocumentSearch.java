package io.committed.ketos.common.data;

import java.util.List;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaleenDocumentSearch extends AbstractGraphQLNode {

  private final DocumentFilter documentFilter;
  private final List<MentionFilter> mentionFilters;
  private final List<RelationFilter> relationFilters;


  @Builder
  public BaleenDocumentSearch(final GraphQLNode parent, final DocumentFilter documentFilter,
      final List<MentionFilter> mentionFilters, final List<RelationFilter> relationFilters) {
    super(parent);
    this.documentFilter = documentFilter;
    this.mentionFilters = mentionFilters;
    this.relationFilters = relationFilters;
  }
}
