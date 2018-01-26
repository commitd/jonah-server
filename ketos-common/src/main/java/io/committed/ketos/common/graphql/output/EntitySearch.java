package io.committed.ketos.common.graphql.output;

import java.util.Collection;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import lombok.Data;

@Data
public class EntitySearch extends AbstractGraphQLNode {

  private final EntityFilter entityFilter;
  private final Collection<MentionFilter> mentionFilter;
}
