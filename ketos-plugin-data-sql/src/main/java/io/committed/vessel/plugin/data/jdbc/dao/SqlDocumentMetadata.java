package io.committed.vessel.plugin.data.jdbc.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class SqlDocumentMetadata {

  @Id
  private Long key;

  @Column(name = "doc_key")
  private String docId;
  private String name;
  private String value;

}
