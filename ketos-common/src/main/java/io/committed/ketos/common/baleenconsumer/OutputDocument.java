package io.committed.ketos.common.baleenconsumer;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class OutputDocument {

  private String externalId;

  private List<OutputDocumentMetadata> metadata;

  private Map<String, Object> properties;

  private String content;

}
