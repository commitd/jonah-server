package io.committed.ketos.common.graphql.output;

import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentSearch extends AbstractGraphQLNode {

  private final DocumentFilter documentFilter;
  private final List<MentionFilter> mentionFilters;
  private final List<RelationFilter> relationFilters;
  private final List<EntityFilter> entityFilters;


  @Builder
  public DocumentSearch(final GraphQLNode parent, final DocumentFilter documentFilter,
      final List<MentionFilter> mentionFilters, final List<EntityFilter> entityFilters,
      final List<RelationFilter> relationFilters) {
    super(parent);
    this.documentFilter = documentFilter;
    this.mentionFilters = mentionFilters == null ? Collections.emptyList() : mentionFilters;
    this.entityFilters = entityFilters == null ? Collections.emptyList() : entityFilters;
    this.relationFilters = relationFilters == null ? Collections.emptyList() : relationFilters;
  }

  @JsonIgnore
  public boolean hasMentions() {
    return !mentionFilters.isEmpty();
  }

  @JsonIgnore
  public boolean hasEntities() {
    return !entityFilters.isEmpty();
  }

  @JsonIgnore
  public boolean hasRelations() {
    return !relationFilters.isEmpty();
  }
}
