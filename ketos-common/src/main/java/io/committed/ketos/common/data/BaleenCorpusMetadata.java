package io.committed.ketos.common.data;

import java.util.Optional;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BaleenCorpusMetadata extends AbstractGraphQLNode {

  private final Optional<String> key;

  public BaleenCorpusMetadata(final GraphQLNode parent, final Optional<String> key) {
    super(parent);
    this.key = key;
  }
}
