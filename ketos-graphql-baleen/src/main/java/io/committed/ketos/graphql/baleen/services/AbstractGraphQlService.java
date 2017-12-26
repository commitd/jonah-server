package io.committed.ketos.graphql.baleen.services;

import java.util.Optional;
import java.util.function.Function;

import io.committed.invest.server.data.providers.DataProvider;
import io.committed.invest.server.data.query.DataHints;
import io.committed.invest.server.data.services.DatasetProviders;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNodeSupport;
import io.committed.ketos.common.graphql.support.GraphQLNode;
import reactor.core.publisher.Flux;

public abstract class AbstractGraphQlService {
  private final DatasetProviders corpusProviders;

  protected AbstractGraphQlService(final DatasetProviders corpusProviders) {
    this.corpusProviders = corpusProviders;
  }

  protected <T extends AbstractGraphQLNodeSupport<T>> Function<T, T> addContext(
      final Object context) {
    return (final T t) -> t.addNodeContext(context);
  }

  protected <T extends DataProvider> Flux<T> getProviders(final BaleenCorpus corpus,
      final Class<T> clazz,
      final DataHints hints) {
    return corpusProviders.findForDataset(corpus.getId(), clazz, hints);
  }

  protected <T extends DataProvider> Flux<T> getProvidersFromContext(
      final AbstractGraphQLNodeSupport<?> node,
      final Class<T> clazz,
      final DataHints hints) {
    if (node.getGqlNode() != null) {
      final GraphQLNode gqlNode = node.getGqlNode();
      final Optional<BaleenCorpus> corpus = gqlNode.findParent(BaleenCorpus.class);
      if (corpus.isPresent()) {
        return corpusProviders.findForDataset(corpus.get().getId(), clazz, hints);
      }
    }
    return Flux.empty();
  }
}
