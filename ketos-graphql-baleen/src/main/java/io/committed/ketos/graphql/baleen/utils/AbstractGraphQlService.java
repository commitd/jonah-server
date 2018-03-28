package io.committed.ketos.graphql.baleen.utils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.committed.ketos.common.graphql.support.GraphQLNode;
import reactor.core.publisher.Flux;

/**
 * Base for GraphQL resolvers which used datasets which offer some convenience methods..
 */
public abstract class AbstractGraphQlService {
  private final DataProviders dataProviders;

  protected AbstractGraphQlService(final DataProviders corpusProviders) {
    this.dataProviders = corpusProviders;
  }

  public DataProviders getDataProviders() {
    return dataProviders;
  }

  protected <T extends AbstractGraphQLNode> Function<T, T> mapAddParent(
      final GraphQLNode parent) {
    return (final T t) -> {
      t.setParent(parent);
      return t;
    };
  }

  protected <T extends AbstractGraphQLNode> Consumer<T> eachAddParent(
      final GraphQLNode parent) {
    return (final T t) -> t.setParent(parent);
  }

  protected <T extends DataProvider> Flux<T> getProviders(final BaleenCorpus corpus,
      final Class<T> clazz, final DataHints hints) {
    return dataProviders.findForDataset(corpus.getId(), clazz, hints);
  }

  protected <T extends DataProvider> Flux<T> getProvidersFromContext(
      @Nullable final GraphQLNode node, final Class<T> clazz, final DataHints hints) {
    if (node != null) {
      final Optional<BaleenCorpus> corpus = node.findParent(BaleenCorpus.class);
      if (corpus.isPresent()) {
        return getProviders(corpus.get(), clazz, hints);
      }
    }
    return Flux.empty();
  }


}
