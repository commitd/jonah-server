package io.committed.ketos.plugins.data.configurer;

import java.util.Collections;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataProviderSpecification {

  // the dataproviderfactory id
  private String factory;

  // One of DocumentProvider, EntityProvider, etc
  private String provider;

  private Map<String, Object> settings = Collections.emptyMap();

}
