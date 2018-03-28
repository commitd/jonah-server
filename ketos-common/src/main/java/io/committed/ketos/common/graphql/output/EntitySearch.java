package io.committed.ketos.common.graphql.output;

import java.util.List;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A full entity search query.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EntitySearch extends AbstractGraphQLNode {

  private EntityFilter entityFilter;

  @Builder
  public EntitySearch(final GraphQLNode parent, final EntityFilter entityFilter,
      final List<MentionFilter> mentionFilters) {
    super(parent);
    this.entityFilter = entityFilter;
  }

}
