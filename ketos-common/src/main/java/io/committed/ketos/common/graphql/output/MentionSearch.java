package io.committed.ketos.common.graphql.output;

import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MentionSearch extends AbstractGraphQLNode {

  private final MentionFilter mentionFilter;

  @Builder
  public MentionSearch(final GraphQLNode parent, final MentionFilter mentionFilter) {
    super(parent);
    this.mentionFilter = mentionFilter;
  }
}
