package io.committed.ketos.test.common.providers.baleen;

import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.references.BaleenMentionReference;

public abstract class AbstractCrudMentionProviderTest
    extends AbstractCrudDataProviderTest<BaleenMentionReference, BaleenMention> {

  @Override
  public BaleenMentionReference getDeleteReference() {
    BaleenMentionReference ref = new BaleenMentionReference();
    ref.setDocumentId("a19f6ed4-87bb-4dc6-919e-596761127082");
    ref.setMentionId("f2712db0-6f0a-4dd1-83b4-2a63c61647fd");
    return ref;
  }

  @Override
  public BaleenMention getSaveItem() {
    BaleenMention mention =
        new BaleenMention(
            "5d9596d9-1edb-4683-b97a-740d1f499dc7",
            0,
            1,
            "test",
            "",
            "testVal",
            "0bd40743-fbec-4484-b9fc-08d89a916840",
            "a19f6ed4-87bb-4dc6-919e-596761127082",
            new PropertiesMap());
    return mention;
  }
}
