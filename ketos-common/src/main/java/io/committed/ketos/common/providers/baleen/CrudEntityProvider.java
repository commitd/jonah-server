package io.committed.ketos.common.providers.baleen;

import io.committed.invest.extensions.data.providers.CrudDataProvider;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.references.BaleenEntityReference;

/**
 * CRUD for Entities
 *
 */
public interface CrudEntityProvider extends CrudDataProvider<BaleenEntityReference, BaleenEntity> {

  @Override
  default String getProviderType() {
    return "CrudEntityProvider";
  }
}
