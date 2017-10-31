package io.committed.ketos.plugins.data.configurer.impl;

import io.committed.ketos.plugins.data.configurer.DataProviderFactory;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DataProvider;
import lombok.Data;

@Data
public abstract class AbstractDataProviderFactory<P extends DataProvider>
    implements DataProviderFactory<P> {

  private String id;
  private final Class<P> dataProvider;

  protected AbstractDataProviderFactory(final String id, final Class<P> clazz) {
    this.id = id;
    this.dataProvider = clazz;
  }


}
