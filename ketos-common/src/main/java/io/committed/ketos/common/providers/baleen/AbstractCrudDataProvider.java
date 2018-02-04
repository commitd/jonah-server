package io.committed.ketos.common.providers.baleen;

import io.committed.invest.extensions.data.providers.DataProvider;

// TODO: Move to invest
public interface AbstractCrudDataProvider<T> extends DataProvider {

  boolean delete(String id);

  boolean save(T item);
}
