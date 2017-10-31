package io.committed.ketos.plugins.data.configurer;

import java.util.Map;

import lombok.Data;

@Data
public class DataProviderSpecification {

  private String id;

  private String provider;

  private Map<String, Object> settings;

}
