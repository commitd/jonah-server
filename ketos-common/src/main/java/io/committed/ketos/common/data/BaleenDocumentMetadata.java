package io.committed.ketos.common.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GraphQL/DTO representation of a Baleen document metadata
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaleenDocumentMetadata {

  private String key;

  private String value;
}

