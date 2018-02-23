package io.committed.ketos.common.graphql.input;

import java.util.Date;
import java.util.Map;
import lombok.Data;

@Data
public class DocumentFilter {

  @Data
  public static class DocumentInfoFilter {
    private String type;
    private String source;
    private String language;
    private String classification;
    private String caveats;
    private String releasability;
    private String publishedId;

    private Date startTimestamp;
    private Date endTimestamp;

  }

  private String id;
  private Map<String, Object> metadata;
  private Map<String, Object> properties;
  private String content;
  private DocumentInfoFilter info;

}
