package io.committed.ketos.plugins.data.baleenmongo;

import io.committed.vessel.extensions.VesselGraphQlExtension;

public class BaleenMongoPlugin implements VesselGraphQlExtension {

  @Override
  public Class<?> getConfiguration() {
    return BaleenMongoConfig.class;
  }

}
