package io.committed.ketos.common.data;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;

/** Corpus Metadata. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BaleenCorpusMetadata extends AbstractGraphQLNode {

  private Optional<String> key;

  public BaleenCorpusMetadata(final GraphQLNode parent, final Optional<String> key) {
    super(parent);
    this.key = key;
  }
}
