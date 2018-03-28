package io.committed.ketos.core.config;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import io.committed.invest.extensions.data.dataset.DataProviderSpecification;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Base for DataDefinitions which generate mutitple Baleen provider.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractMultipleDataProviderDataDefinition extends AbstractKetosDataDefinition {

  private static final String BALEEN_PREFIX = "baleen-";

  private boolean edittable = false;
  private String factoryMiddle;

  public AbstractMultipleDataProviderDataDefinition(final String id, final String name, final String description,
      final String factoryMiddle) {
    super(id, name, description);
    this.factoryMiddle = factoryMiddle;
  }

  @Override
  protected List<DataProviderSpecification> createProviders() {
    final List<DataProviderSpecification> providers = new LinkedList<>();

    final Map<String, Object> baseSettings = getBaseSettings();

    addProvider(providers, BALEEN_PREFIX + factoryMiddle + "-documents",
        merge(baseSettings, getDocumentProviderSettings()));
    addProvider(providers, BALEEN_PREFIX + factoryMiddle + "-entities",
        merge(baseSettings, getEntityProviderSettings()));
    addProvider(providers, BALEEN_PREFIX + factoryMiddle + "-relations",
        merge(baseSettings, getRelationProviderSettings()));
    addProvider(providers, BALEEN_PREFIX + factoryMiddle + "-mentions",
        merge(baseSettings, getMentionProviderSettings()));
    addProvider(providers, BALEEN_PREFIX + factoryMiddle + "-metadata",
        merge(baseSettings, getMetadataProviderSettings()));

    if (edittable) {
      addProvider(providers, BALEEN_PREFIX + factoryMiddle + "-crud-documents",
          merge(baseSettings, getCrudDocumentProviderSettings()));
      addProvider(providers, BALEEN_PREFIX + factoryMiddle + "-crud-entities",
          merge(baseSettings, getCrudEntityProviderSettings()));
      addProvider(providers, BALEEN_PREFIX + factoryMiddle + "-crud-relations",
          merge(baseSettings, getCrudRelationProviderSettings()));
      addProvider(providers, BALEEN_PREFIX + factoryMiddle + "-crud-mentions",
          merge(baseSettings, getCrudMentionProviderSettings()));
    }

    return providers;
  }

  protected abstract Map<String, Object> getCrudMentionProviderSettings();

  protected abstract Map<String, Object> getCrudRelationProviderSettings();

  protected abstract Map<String, Object> getCrudEntityProviderSettings();

  protected abstract Map<String, Object> getCrudDocumentProviderSettings();

  protected abstract Map<String, Object> getMentionProviderSettings();

  protected abstract Map<String, Object> getRelationProviderSettings();

  protected abstract Map<String, Object> getEntityProviderSettings();

  protected abstract Map<String, Object> getDocumentProviderSettings();

  protected abstract Map<String, Object> getBaseSettings();

  protected abstract Map<String, Object> getMetadataProviderSettings();
}
