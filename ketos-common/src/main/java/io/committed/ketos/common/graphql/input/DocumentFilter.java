package io.committed.ketos.common.graphql.input;

import java.util.Date;
import io.committed.invest.core.dto.collections.PropertiesList;
import io.committed.invest.core.dto.collections.PropertiesMap;
import lombok.Data;

/**
 * Search query for a document.
 */
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
  private PropertiesList metadata;
  private PropertiesMap properties;
  private String content;
  private DocumentInfoFilter info;

}
