package io.committed.ketos.common.data;

import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.leangen.graphql.annotations.GraphQLId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Builder
public class BaleenCorpus extends AbstractGraphQLNode {

  @GraphQLId
  private final String id;

  private final String name;

  private final String description;
}
