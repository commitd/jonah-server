package io.committed.ketos.plugins.data.mongo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.committed.ketos.common.data.BaleenDocumentInfo;
import lombok.Data;

@Data
@Document(collection = "documents")
public class MongoDocument {

  @Id
  private String id;
  private String externalId;
  // TODO: This should be a MongoDocumentInfo as it needs to deserialise to db, rather than graphql
  private BaleenDocumentInfo document;
  private List<String> publishedIds;
  private Map<String, Object> metadata = new HashMap<>();
  @TextIndexed
  private String content;

  public io.committed.ketos.common.data.BaleenDocument toDocument() {
    final io.committed.ketos.common.data.BaleenDocument d =
        new io.committed.ketos.common.data.BaleenDocument();
    d.setId(getExternalId());
    d.setInfo(getDocument());
    d.setPublishedIds(getPublishedIds());
    d.setMetadata(getMetadata());
    d.setContent(getContent());
    return d;
  }
}
