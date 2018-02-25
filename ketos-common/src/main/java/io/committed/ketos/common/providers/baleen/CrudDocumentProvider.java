package io.committed.ketos.common.providers.baleen;

import io.committed.invest.extensions.data.providers.CrudDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.references.BaleenDocumentReference;

public interface CrudDocumentProvider extends CrudDataProvider<BaleenDocumentReference, BaleenDocument> {

  @Override
  default String getProviderType() {
    return "CrudDocumentProvider";
  }

}
