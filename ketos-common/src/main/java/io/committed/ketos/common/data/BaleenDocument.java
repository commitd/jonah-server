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
  private final BaleenDocumentInfo info;
  private final List<String> publishedIds;
  private final Map<String, Object> metadata;
  private final String content;

  @GraphQLQuery(name = "length", description = "Length of document content in characters")
  public int length() {
    return content.length();
  }

  // THis all args constructor is required for the Builder
  public BaleenDocument(final String id, final BaleenDocumentInfo documentInfo, final List<String> publishedIds,
      final Map<String, Object> metadata, final String content) {
    this.id = id;
    this.info = documentInfo;
    this.publishedIds = publishedIds;
    this.metadata = metadata == null ? Collections.emptyMap() : metadata;
    this.content = content;

    // Ensure the setParent is correct on documentinfo
    if (this.info != null) {
      this.info.setParent(this);
    }
  }


}
