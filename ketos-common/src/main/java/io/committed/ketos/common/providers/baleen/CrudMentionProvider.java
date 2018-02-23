package io.committed.ketos.common.providers.baleen;

import io.committed.invest.extensions.data.providers.AbstractCrudDataProvider;
import io.committed.ketos.common.data.BaleenMention;

public interface CrudMentionProvider extends AbstractCrudDataProvider<BaleenMention> {

  @Override
  default String getProviderType() {
    return "CrudMentionProvider";
  }
}
