package io.committed.ketos.common.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BaleenDocumentMetadata {

  private final String key;

  private final String value;
}
