package io.committed.ketos.plugins.data.baleen;

import io.committed.graphql.support.AbstractGraphQLNodeSupport;
import io.leangen.graphql.annotations.GraphQLId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaleenCorpus extends AbstractGraphQLNodeSupport<BaleenCorpus> {

  @GraphQLId
  private String id;

  private String name;
}
