package io.committed.ketos.plugins.data.mongo;

import io.committed.vessel.extensions.VesselGraphQlExtension;

public class BaleenMongoPlugin implements VesselGraphQlExtension {

  @Override
  public Class<?> getConfiguration() {
    return BaleenMongoConfig.class;
  }

}
