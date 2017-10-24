package io.committed.ketos.data.elasticsearch;

import io.committed.vessel.extensions.VesselGraphQlExtension;

public class BaleenElasticsearchPlugin implements VesselGraphQlExtension {

  @Override
  public Class<?> getConfiguration() {
    return BaleenElasticsearchConfig.class;
  }

}
