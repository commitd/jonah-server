package io.committed.ketos.ui.documentsearch;


import io.committed.vessel.extensions.VesselUiExtension;

/**
 * Extension point for CorpusSummary.
 *
 */
public class DocumentSearchPlugin implements VesselUiExtension {

  @Override
  public String getId() {
    return "DocumentSearch";
  }

  @Override
  public String getName() {
    return "Document search";
  }

  @Override
  public String getDescription() {
    return "Find documents by content";
  }

  @Override
  public String getIcon() {
    return "search";
  }

  @Override
  public Class<?> getSettings() {
    return DocumentSearchSettings.class;
  }

  @Override
  public String getStaticResourcePath() {
    // Do not change this without also changing the pom.xml copy-resources
    // as Maven will copy the output from the JS build into this location
    return "/ui/DocumentSearch/";
  }
}
