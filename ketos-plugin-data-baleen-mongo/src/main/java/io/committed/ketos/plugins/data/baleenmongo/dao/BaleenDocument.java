package io.committed.ketos.plugins.data.baleenmongo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.committed.ketos.plugins.graphql.baleen.DocumentInfo;
import lombok.Data;

@Data
@Document(collection = "documents")
public class BaleenDocument {

  @Id
  private String id;
  private String externalId;
  private DocumentInfo document;
  private List<String> publishedIds;
  private Map<String, Object> metadata = new HashMap<>();
  @TextIndexed
  private String content;

  public io.committed.ketos.plugins.graphql.baleen.Document toDocument() {
    final io.committed.ketos.plugins.graphql.baleen.Document d =
        new io.committed.ketos.plugins.graphql.baleen.Document();
    d.setId(getExternalId());
    d.setInfo(getDocument());
    d.setPublishedIds(getPublishedIds());
    d.setMetadata(getMetadata());
    d.setContent(getContent());
    return d;
  }
}
