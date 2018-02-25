package io.committed.ketos.common.providers.baleen;

import io.committed.invest.extensions.data.providers.CrudDataProvider;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.references.BaleenMentionReference;

public interface CrudMentionProvider extends CrudDataProvider<BaleenMentionReference, BaleenMention> {

  @Override
  default String getProviderType() {
    return "CrudMentionProvider";
  }
}
