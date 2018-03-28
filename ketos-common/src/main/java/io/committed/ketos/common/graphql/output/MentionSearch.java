package io.committed.ketos.common.graphql.output;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;

/** A full mention search query. */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MentionSearch extends AbstractGraphQLNode {

  private MentionFilter mentionFilter;

  @Builder
  public MentionSearch(final GraphQLNode parent, final MentionFilter mentionFilter) {
    super(parent);
    this.mentionFilter = mentionFilter;
  }
}
