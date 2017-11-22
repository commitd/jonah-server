package io.committed.vessel.plugin.data.jpa.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class JpaDocumentMetadata {

  @Id
  private Long key;

  @Column(name = "doc_key")
  private String docId;
  private String name;
  private String value;

}
