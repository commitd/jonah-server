package io.committed.ketos.plugins.data.baleen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.committed.graphql.support.AbstractGraphQLNodeSupport;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class BaleenDocument extends AbstractGraphQLNodeSupport<BaleenDocument> {

  @GraphQLId
  private String id;
  private BaleenDocumentInfo info;
  private List<String> publishedIds;
  private Map<String, Object> metadata = new HashMap<>();
  private String content;

  @GraphQLQuery(name = "length")
  public int length() {
    return content.length();
  }

}