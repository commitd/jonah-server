package io.committed.ketos.common.graphql.output;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EntitySearch extends AbstractGraphQLNode {

  private final EntityFilter entityFilter;
  private final Collection<MentionFilter> mentionFilters;

  @Builder
  public EntitySearch(final GraphQLNode parent, final EntityFilter entityFilter,
      final List<MentionFilter> mentionFilters) {
    super(parent);
    this.entityFilter = entityFilter;
    this.mentionFilters = mentionFilters == null ? Collections.emptyList() : mentionFilters;
  }

}
