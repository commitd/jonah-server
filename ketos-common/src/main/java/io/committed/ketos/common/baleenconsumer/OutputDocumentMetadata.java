package io.committed.ketos.common.baleenconsumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Baleen's output format for document metadata. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputDocumentMetadata {

  private String key;

  private String value;
}
