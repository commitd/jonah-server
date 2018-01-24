package io.committed.ketos.common.graphql.input;

import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Data;


@Data
public class DocumentProbe {

  @Data
  public static class DocumentInfoProbe {
    private String type;
    private String source;
    private String language;
    private String classification;
    private String caveats;
    private String releasability;

    private Date timestamp;
  }

  private String id;
  private DocumentInfoProbe info;
  private List<String> publishedIds;
  private Map<String, Object> metadata;
  private String content;

}
