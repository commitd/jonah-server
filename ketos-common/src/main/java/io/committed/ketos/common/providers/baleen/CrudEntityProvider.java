package io.committed.ketos.common.providers.baleen;

import io.committed.ketos.common.data.BaleenEntity;

public interface CrudEntityProvider extends AbstractCrudDataProvider<BaleenEntity> {

  @Override
  default String getProviderType() {
    return "CrudEntityProvider";
  }
}
