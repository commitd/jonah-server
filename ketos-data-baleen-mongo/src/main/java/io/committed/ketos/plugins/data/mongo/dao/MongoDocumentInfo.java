package io.committed.ketos.plugins.data.mongo.dao;

import java.util.Date;
import java.util.List;
import io.committed.ketos.common.data.BaleenDocumentInfo;
import lombok.Data;

@Data
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

}
