package io.committed.ketos.common.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class BaleenDocument extends AbstractGraphQLNode {

  @GraphQLId
  private final String id;

  private final List<BaleenDocumentMetadata> metadata;

  private final String content;

  private final Map<String, Object> properties;

  @GraphQLQuery(name = "length", description = "Length of document content in characters")
  public int length() {
    return content.length();
  }

  // THis all args constructor is required for the Builder
  public BaleenDocument(final String id,
      final List<BaleenDocumentMetadata> metadata, final String content, final Map<String, Object> properties) {
    this.id = id;
    this.metadata = metadata == null ? Collections.emptyList() : metadata;
    this.content = content;
    this.properties = properties == null ? Collections.emptyMap() : properties;
  }

}
