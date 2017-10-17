package io.committed.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.committed.ketos.dao.BaleenDocument;
import io.committed.ketos.dao.DocumentInfo;
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

  public Document(BaleenDocument baleen) {
    id = baleen.getExternalId();
    info = baleen.getDocument();
    publishedIds = baleen.getPublishedIds();
    metadata = baleen.getMetadata();
    content = baleen.getContent();
  }


  @GraphQLQuery(name = "length")
  public int length() {
    return content.length();
  }

}
