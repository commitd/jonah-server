package io.committed.ketos.test.common.providers.baleen;

import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.references.BaleenEntityReference;

public abstract class AbstractCrudEntityProviderTest
    extends AbstractCrudDataProviderTest<BaleenEntityReference, BaleenEntity> {

  @Override
  public BaleenEntityReference getDeleteReference() {
    BaleenEntityReference ref = new BaleenEntityReference();
    ref.setDocumentId("a19f6ed4-87bb-4dc6-919e-596761127082");
    ref.setEntityId("42ad69a0-f11b-449e-947f-fe91f52be3d7");
    return ref;
  }

  @Override
  public BaleenEntity getSaveItem() {
    return new BaleenEntity("0bd40743-fbec-4484-b9fc-08d89a916840", "a19f6ed4-87bb-4dc6-919e-596761127082", "Test", "",
        "newval", new PropertiesMap());
  }


}
