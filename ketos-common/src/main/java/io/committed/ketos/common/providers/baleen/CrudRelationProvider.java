package io.committed.ketos.common.providers.baleen;

import io.committed.invest.extensions.data.providers.AbstractCrudDataProvider;
import io.committed.ketos.common.data.BaleenRelation;

public interface CrudRelationProvider extends AbstractCrudDataProvider<BaleenRelation> {

  @Override
  default String getProviderType() {
    return "CrudRelationProvider";
  }
}
