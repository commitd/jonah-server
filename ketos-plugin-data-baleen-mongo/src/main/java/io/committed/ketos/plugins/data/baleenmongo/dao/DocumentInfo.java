package io.committed.ketos.dao;

import java.util.List;

import lombok.Data;

@Data
public class DocumentInfo {
  private String type;
  private String source;
  private String language;
  private Long ts;
  private String classification;
  private List<String> caveats;
  private List<String> releasability;
}
