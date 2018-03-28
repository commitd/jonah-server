package io.committed.ketos.common.baleenconsumer;

import java.util.Map;
import java.util.Set;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Baleen's output format for an entity. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutputEntity {

  private String docId;

  private String externalId;

  private String type;

  private String subType;

  private String value;

  private Map<String, Object> properties;

  private Set<String> mentionIds;
}
