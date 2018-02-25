package io.committed.ketos.common.providers.baleen;

import io.committed.invest.extensions.data.providers.AbstractCrudDataProvider;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.references.BaleenEntityReference;

public interface CrudEntityProvider extends AbstractCrudDataProvider<BaleenEntityReference, BaleenEntity> {

  @Override
  default String getProviderType() {
    return "CrudEntityProvider";
  }
}
