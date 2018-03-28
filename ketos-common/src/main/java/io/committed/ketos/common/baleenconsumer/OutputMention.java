package io.committed.ketos.common.baleenconsumer;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Baleen's output format for a document.
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutputMention {

  private String docId;

  private String externalId;

  private String entityId;

  private int begin;

  private int end;

  private String type;

  private String subType;

  private String value;

  private Map<String, Object> properties;


}
