package io.committed.vessel.plugin.data.jdbc;

import io.committed.vessel.extensions.VesselGraphQlExtension;

public class BaleenJdbcPlugin implements VesselGraphQlExtension {

  @Override
  public Class<?> getConfiguration() {
    return BaleenJdbcConfig.class;
  }

}
