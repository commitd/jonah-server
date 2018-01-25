package io.committed.ketos.plugins.data.mongo.dao;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import io.committed.ketos.common.data.BaleenDocumentInfo;
import io.committed.ketos.common.graphql.input.DocumentProbe.DocumentInfoProbe;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MongoDocumentInfo {

  private String type;
  private String source;
  private String language;
  private Date timestamp;
  private String classification;
  private List<String> caveats;
  private List<String> releasability;

  public BaleenDocumentInfo toBaleenDocumentInfo() {
    return BaleenDocumentInfo.builder()
        .type(type)
        .source(source)
        .language(language)
        .timestamp(timestamp)
        .classification(classification)
        .caveats(caveats)
        .releasability(releasability)
        .build();
  }

  public MongoDocumentInfo(final DocumentInfoProbe info) {
    if (info != null) {
      type = info.getType();
      source = info.getSource();
      language = info.getLanguage();
      timestamp = info.getTimestamp();
      classification = info.getClassification();
      caveats = Collections.singletonList(info.getCaveats());
      releasability = Collections.singletonList(info.getReleasability());
    }
  }

}
