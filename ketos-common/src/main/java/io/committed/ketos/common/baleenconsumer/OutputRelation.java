package io.committed.ketos.common.baleenconsumer;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutputRelation {

  private String docId;

  private String externalId;

  private String type;

  private String subType;

  private String value;

  private OutputMention source;

  private OutputMention target;

  private Map<String, Object> properties;

  private int begin;

  private int end;

}


