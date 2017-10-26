package io.committed.ketos.plugins.data.baleen;

import java.util.List;

import lombok.Data;

@Data
public class BaleenDocumentInfo {
  private String type;
  private String source;
  private String language;
  private Long ts;
  private String classification;
  private List<String> caveats;
  private List<String> releasability;
}
