package io.committed.ketos.test.common.providers.baleen;

import java.util.Collections;

import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.references.BaleenDocumentReference;

public abstract class AbstractCrudDocumentProviderTest
    extends AbstractCrudDataProviderTest<BaleenDocumentReference, BaleenDocument> {

  @Override
  public BaleenDocumentReference getDeleteReference() {
    BaleenDocumentReference ref = new BaleenDocumentReference();
    ref.setDocumentId("a19f6ed4-87bb-4dc6-919e-596761127082");
    return ref;
  }

  @Override
  public BaleenDocument getSaveItem() {
    BaleenDocument document =
        new BaleenDocument(
            "cffa7e3a-4664-4308-b97b-cb7a1f96e7ff",
            Collections.EMPTY_LIST,
            "",
            new PropertiesMap());
    return document;
  }
}
