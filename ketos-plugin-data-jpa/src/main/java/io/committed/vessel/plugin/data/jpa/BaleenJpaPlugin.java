package io.committed.vessel.plugin.data.jpa;

import io.committed.vessel.extensions.VesselGraphQlExtension;

public class BaleenJpaPlugin implements VesselGraphQlExtension {

  @Override
  public Class<?> getConfiguration() {
    return BaleenJpaConfig.class;
  }

}
