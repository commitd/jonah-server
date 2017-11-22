package io.committed.ketos.common.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.committed.ketos.common.graphql.support.AbstractGraphQLNodeSupport;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaleenDocument extends AbstractGraphQLNodeSupport<BaleenDocument> {

  @GraphQLId
  private String id;
  private BaleenDocumentInfo info;
  private List<String> publishedIds;
  private Map<String, Object> metadata = new HashMap<>();
  private String content;

  @GraphQLQuery(name = "length", description = "Length of document content in characters")
  public int length() {
    return content.length();
  }

}
