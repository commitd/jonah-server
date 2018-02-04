package io.committed.ketos.graphql.baleen.mutations;

import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.providers.baleen.AbstractCrudDataProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// TODO: move to invest?
public abstract class AbstractCrudMutation<T, P extends AbstractCrudDataProvider<T>> extends AbstractGraphQlService {

  private final Class<T> itemClass;
  private final Class<P> providerClass;

  public AbstractCrudMutation(final DataProviders dataProviders, final Class<T> itemClass,
      final Class<P> providerClass) {
    super(dataProviders);
    this.itemClass = itemClass;
    this.providerClass = providerClass;
  }

  protected Class<T> getItemClass() {
    return itemClass;
  }

  public Class<P> getProviderClass() {
    return providerClass;
  }

  protected Flux<DataProvider> delete(final String id, final DataHints hints) {

    return getDataProviders()
        .find(providerClass, hints)
        .flatMap(p -> {
          final boolean delete = p.delete(id);
          if (delete) {
            return Mono.just(p);
          } else {
            return Mono.empty();
          }
        });

  }

  public Flux<DataProvider> save(final T t, final DataHints hints) {

    return getDataProviders()
        .find(providerClass, hints)
        .flatMap(p -> {
          final boolean saved = p.save(t);
          if (saved) {
            return Mono.just(p);
          } else {
            return Mono.empty();
          }
        });

  }

}
