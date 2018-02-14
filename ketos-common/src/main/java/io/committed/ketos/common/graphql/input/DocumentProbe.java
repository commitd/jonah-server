package io.committed.ketos.common.graphql.input;

import java.util.Date;
import java.util.List;
import java.util.Map;
import io.committed.ketos.common.graphql.input.DocumentFilter.DocumentInfoFilter;
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
    private String publishedId;

    private Date timestamp;

    public DocumentInfoFilter toFilter() {
      final DocumentInfoFilter filter = new DocumentInfoFilter();

      filter.setCaveats(caveats);
      filter.setClassification(classification);
      filter.setReleasability(releasability);
      filter.setLanguage(language);
      filter.setSource(source);
      filter.setEndTimestamp(timestamp);
      filter.setStartTimestamp(timestamp);
      filter.setType(type);
      filter.setPublishedId(publishedId);

      return filter;
    }
  }

  private String id;
  private DocumentInfoProbe info;
  private List<String> publishedIds;
  private Map<String, Object> metadata;
  private Map<String, Object> properties;

  private String content;

  public DocumentFilter toDocumentFilter() {
    final DocumentFilter filter = new DocumentFilter();


    filter.setId(id);
    filter.setContent(content);
    filter.setMetadata(metadata);
    filter.setInfo(info != null ? info.toFilter() : null);
    filter.setProperties(properties);
    return filter;
  }
}
