package io.committed.ketos.common.dataproviders.configuration;

import io.committed.ketos.common.providers.baleen.DataProvider;
import lombok.Data;

@Data
public abstract class AbstractDataProviderFactory<P extends DataProvider>
    implements DataProviderFactory<P> {

  private String id;

  private final Class<P> dataProvider;

  private final String datasource;

  protected AbstractDataProviderFactory(final String id, final Class<P> clazz,
      final String datasource) {
    this.id = id;
    this.dataProvider = clazz;
    this.datasource = datasource;
  }


}
