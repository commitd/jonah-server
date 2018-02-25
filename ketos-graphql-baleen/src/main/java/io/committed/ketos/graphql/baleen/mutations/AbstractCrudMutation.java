package io.committed.ketos.graphql.baleen.mutations;

import java.util.Optional;
import io.committed.invest.extensions.data.providers.AbstractCrudDataProvider;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractCrudMutation<R, T, P extends AbstractCrudDataProvider<R, T>>
    extends AbstractGraphQlService {

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

  protected Flux<DataProvider> delete(final Optional<String> datasetId, final R reference, final DataHints hints) {

    return getProviders(datasetId, hints)
        .flatMap(p -> {
          final boolean delete = p.delete(reference);
          if (delete) {
            return Mono.just(p);
          } else {
            return Mono.empty();
          }
        });

  }

  public Flux<DataProvider> save(final Optional<String> datasetId, final T t, final DataHints hints) {

    return getProviders(datasetId, hints)
        .flatMap(p -> {
          final boolean saved = p.save(t);
          if (saved) {
            return Mono.just(p);
          } else {
            return Mono.empty();
          }
        });

  }

  protected Flux<P> getProviders(final Optional<String> datasetId, final DataHints hints) {
    if (datasetId.isPresent()) {
      return getDataProviders().findForDataset(datasetId.get(), providerClass, hints);
    } else {
      return getDataProviders()
          .find(providerClass, hints);
    }
  }

}
