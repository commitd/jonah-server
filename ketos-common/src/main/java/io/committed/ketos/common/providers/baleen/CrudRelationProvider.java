package io.committed.ketos.common.providers.baleen;

import io.committed.invest.extensions.data.providers.AbstractCrudDataProvider;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.references.BaleenRelationReference;

public interface CrudRelationProvider extends AbstractCrudDataProvider<BaleenRelationReference, BaleenRelation> {

  @Override
  default String getProviderType() {
    return "CrudRelationProvider";
  }
}
