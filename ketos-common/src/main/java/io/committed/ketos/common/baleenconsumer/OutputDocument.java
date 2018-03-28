package io.committed.ketos.common.baleenconsumer;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Baleen's output format for a document. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutputDocument {

  private String externalId;

  private List<OutputDocumentMetadata> metadata;

  private Map<String, Object> properties;

  private String content;
}
