package io.committed.vessel.plugin.data.jdbc.dao;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Entity
@Data
public class SqlDocument {
  @Id
  private Long key;

  private String externalId;
  private String type;
  private String source;
  private String content;
  private String language;
  private Date processed;
  private String classification;
  private List<String> caveats;
  private List<String> releasability;
}
