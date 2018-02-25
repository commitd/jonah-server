package io.committed.ketos.common.providers.baleen;

import io.committed.invest.extensions.data.providers.AbstractCrudDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.references.BaleenDocumentReference;

public interface CrudDocumentProvider extends AbstractCrudDataProvider<BaleenDocumentReference, BaleenDocument> {

  @Override
  default String getProviderType() {
    return "CrudDocumentProvider";
  }

}
