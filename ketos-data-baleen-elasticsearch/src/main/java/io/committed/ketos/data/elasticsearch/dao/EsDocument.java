package io.committed.ketos.data.elasticsearch.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentInfo;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EsDocument {

  private String content;
  private String language;
  private String externalId;
  private Date dateAccessed;
  private String sourceUri;
  private String docType;
  private String classification;
  private List<String> caveats;
  private List<String> releasability;
  private List<String> publishedId;

  private Map<String, Object> metadata;

  private List<EsEntity> entities;

  private List<EsRelation> relations;

  public BaleenDocument toBaleenDocument() {
    return BaleenDocument.builder()
        .content(content)
        .id(externalId)
        .metadata(metadata)
        .publishedIds(publishedId)
        .info(BaleenDocumentInfo.builder()
            .caveats(caveats)
            .classification(classification)
            .language(language)
            .releasability(releasability)
            .source(sourceUri)
            .timestamp(new Date(dateAccessed.getTime()))
            .type(docType)
            .build())
        .build();
  }
}