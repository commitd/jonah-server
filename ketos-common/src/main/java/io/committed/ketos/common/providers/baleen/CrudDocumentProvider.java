package io.committed.ketos.common.providers.baleen;

import io.committed.ketos.common.data.BaleenDocument;

public interface CrudDocumentProvider extends AbstractCrudDataProvider<BaleenDocument> {

  @Override
  default String getProviderType() {
    return "CrudDocumentProvider";
  }

}