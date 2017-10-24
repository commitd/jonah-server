package io.committed.vessel.plugin.data.jdbc.dao;

import lombok.Data;

@Entity
@Data
public class SqlDocumentMetadata {

  @Id
  private Long key;

  @ManyToOne
  private SqlDocument doc_key;
  private String name;
  private String value;

}
