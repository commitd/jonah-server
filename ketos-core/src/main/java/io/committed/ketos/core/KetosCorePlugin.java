package io.committed.ketos.core;

import io.committed.vessel.extensions.VesselGraphQlExtension;

public class KetosCorePlugin implements VesselGraphQlExtension {

  @Override
  public Class<?> getConfiguration() {
    return KetosCoreConfig.class;
  }

}
