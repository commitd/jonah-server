package io.committed.ketos.test.common.providers.baleen;

import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.references.BaleenRelationReference;

public abstract class AbstractCrudRelationProviderTest
    extends AbstractCrudDataProviderTest<BaleenRelationReference, BaleenRelation> {

  @Override
  public BaleenRelationReference getDeleteReference() {
    BaleenRelationReference rel = new BaleenRelationReference();
    rel.setDocumentId("a19f6ed4-87bb-4dc6-919e-596761127082");
    rel.setRelationId("9dec8723-5ba5-4fb1-8718-6c12586aaa89");
    return rel;
  }

  @Override
  public BaleenRelation getSaveItem() {
    BaleenRelation relation = new BaleenRelation("9dec8723-5ba5-4fb1-8718-6c12586aaa89",
        "a19f6ed4-87bb-4dc6-919e-596761127082", 0, 1, "test", "test", "value", null, null, new PropertiesMap());
    return relation;
  }

}
