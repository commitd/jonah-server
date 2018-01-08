package io.committed.ketos.common.data;

import java.util.Optional;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNodeSupport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BaleenCorpusMetadata extends AbstractGraphQLNodeSupport<BaleenCorpusMetadata> {

  private Optional<String> key;
}
