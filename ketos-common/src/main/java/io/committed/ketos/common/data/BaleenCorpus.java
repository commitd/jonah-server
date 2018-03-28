package io.committed.ketos.common.data;

import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.leangen.graphql.annotations.GraphQLId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents the output of Baleen.
 *
 * This is effect is an Invest dataset. It will typically be the output of one or more Baleen
 * pipelines.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaleenCorpus extends AbstractGraphQLNode {

  @GraphQLId
  private String id;

  private String name;

  private String description;
}
