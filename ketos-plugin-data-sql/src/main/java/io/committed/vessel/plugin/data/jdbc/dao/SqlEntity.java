package io.committed.vessel.plugin.data.jdbc.dao;

import java.util.List;

// TODO: Also geojson entity table.
public class SqlEntity {
  @Id
  private Long key;

  @ManyToOne
  private SqlDocument doc_key;

  private List<String> externalId;
  private String type;
  private List<String> value;
}
