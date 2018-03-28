package io.committed.ketos.common.graphql.input;

import java.util.Date;
import java.util.List;

import lombok.Data;

import io.committed.invest.core.dto.collections.PropertiesList;
import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.ketos.common.graphql.input.DocumentFilter.DocumentInfoFilter;

/** Search by example document. */
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
  private PropertiesList metadata;
  private PropertiesMap properties;

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
