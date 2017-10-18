package io.committed.ketos.plugins.data.baleenmongo.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "relations")
@Data
public class BaleenRelation {
  @Id
  private String id;

  private String externalId;

  private String docId;

  @Indexed
  private String source;

  @Indexed
  private String target;

  private int begin;

  private int end;

  private String type;

  private String relationshipType;

  private String relationSubtype;

  private String value;

  private double confidence;

}
