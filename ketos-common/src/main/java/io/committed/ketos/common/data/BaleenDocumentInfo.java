package io.committed.ketos.common.data;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaleenDocumentInfo {
  private String type;
  private String source;
  private String language;
  private Long ts;
  private String classification;
  private List<String> caveats;
  private List<String> releasability;
}