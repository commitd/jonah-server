package io.committed.ketos.common.providers.baleen;

import io.committed.invest.extensions.data.providers.CrudDataProvider;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.references.BaleenRelationReference;

/** CRUD for Relations */
public interface CrudRelationProvider
    extends CrudDataProvider<BaleenRelationReference, BaleenRelation> {

  @Override
  default String getProviderType() {
    return "CrudRelationProvider";
  }
}
