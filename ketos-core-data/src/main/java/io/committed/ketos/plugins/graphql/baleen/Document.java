package io.committed.ketos.plugins.graphql.baleen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Data;


@Data
public class Document {

  @GraphQLId
  private String id;
  private DocumentInfo info;
  private List<String> publishedIds;
  private Map<String, Object> metadata = new HashMap<>();
  private String content;

  @GraphQLQuery(name = "length")
  public int length() {
    return content.length();
  }

}
