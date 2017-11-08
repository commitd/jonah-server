package io.committed.ketos.ui.corpussummary;

import io.committed.vessel.extensions.VesselUiExtension;

/**
 * Extension point for CorpusSummary.
 * 
 */
public class CorpusSummaryPlugin implements VesselUiExtension {

  @Override
  public String getId() {
    return "CorpusSummary";
  }

  @Override
  public Class<?> getSettings() {
    return CorpusSummarySettings.class;
  }

  // TODO: You should override to provide additional information such name, description and logo

  @Override
  public String getStaticResourcePath() {
    // Do not change this without also changing the pom.xml copy-resources
    // as Maven will copy the output from the JS build into this location
    return "/ui/CorpusSummary/";
  }
}
