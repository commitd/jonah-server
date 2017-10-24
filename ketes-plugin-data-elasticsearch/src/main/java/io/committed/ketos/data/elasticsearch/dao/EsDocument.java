package io.committed.ketos.data.elasticsearch.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

}