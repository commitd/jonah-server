package io.committed.ketos.graphql.baleen.mutations;

import java.util.Optional;
import io.committed.invest.extensions.data.providers.CrudDataProvider;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Base class for crud mutations.
 *
 * @param <R> the reference to an item for delivery
 * @param <T> the item which will be saved
 * @param <P> the data provider
 */
public abstract class AbstractCrudMutation<R, T, P extends CrudDataProvider<R, T>>
extends AbstractGraphQlService {

  private final Class<T> itemClass;
  private final Class<P> providerClass;

  public AbstractCrudMutation(
      final DataProviders dataProviders, final Class<T> itemClass, final Class<P> providerClass) {
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

  protected Flux<DataProvider> delete(
      final Optional<String> datasetId, final R r, final DataHints hints) {
    return getProviders(datasetId, hints)
        .flatMap(
            p -> {
              final boolean done = p.delete(r);
              return done ? Mono.just(p) : Mono.empty();
            });
  }

  public Flux<DataProvider> save(
      final Optional<String> datasetId, final T t, final DataHints hints) {

    return getProviders(datasetId, hints)
        .flatMap(
            p -> {
              final boolean done = p.save(t);
              return done ? Mono.just(p) : Mono.empty();
            });
  }

  protected Flux<P> getProviders(final Optional<String> datasetId, final DataHints hints) {

    // We want to apply this to all the datasources we
    // have (dupliation == true) unless the user asks otherwise
    final DataHints hintsToApply =
        hints != null ? hints : new DataHints(null, null, true);

    if (datasetId.isPresent()) {
      return getDataProviders().findForDataset(datasetId.get(), providerClass, hintsToApply);
    } else {
      return getDataProviders().find(providerClass, hintsToApply);
    }
  }
}
