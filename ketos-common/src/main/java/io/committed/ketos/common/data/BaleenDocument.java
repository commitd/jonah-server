package io.committed.ketos.common.data;

import java.util.Collections;
import java.util.List;
import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
public class BaleenDocument extends AbstractGraphQLNode {

  @GraphQLId
  private String id;

  private List<BaleenDocumentMetadata> metadata;

  private String content;

  private PropertiesMap properties;

  @GraphQLQuery(name = "length", description = "Length of document content in characters")
  public int length() {
    return content.length();
  }

  // THis all args constructor is required for the Builder
  public BaleenDocument(final String id, final List<BaleenDocumentMetadata> metadata,
      final String content, final PropertiesMap properties) {
    this.id = id;
    this.metadata = metadata != null ? metadata : Collections.emptyList();
    this.content = content;
    this.properties = properties == null ? new PropertiesMap() : properties;
  }

  public Mono<String> findSingleFromMetadata(final String key) {
    return findAllFromMetadata(key).next();
  }

  public Flux<String> findAllFromMetadata(final String key) {
    return Flux.fromIterable(metadata)
        .filter(m -> key.equalsIgnoreCase(m.getKey()))
        .map(BaleenDocumentMetadata::getValue);
  }

}
