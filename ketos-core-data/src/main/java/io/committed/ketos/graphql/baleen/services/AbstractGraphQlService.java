package io.committed.ketos.graphql.baleen.services;

import java.util.Optional;
import java.util.function.Function;

import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNodeSupport;
import io.committed.ketos.common.graphql.support.GraphQLNode;
import io.committed.ketos.common.providers.baleen.DataProvider;
import io.committed.ketos.core.services.CorpusProviders;
import reactor.core.publisher.Flux;

public abstract class AbstractGraphQlService {
  private final CorpusProviders corpusProviders;

  protected AbstractGraphQlService(final CorpusProviders corpusProviders) {
    this.corpusProviders = corpusProviders;
  }

  protected <T extends AbstractGraphQLNodeSupport<T>> Function<T, T> addContext(
      final Object context) {
    return (final T t) -> t.addNodeContext(context);
  }

  protected <T extends DataProvider> Flux<T> getProviders(final BaleenCorpus corpus,
      final Class<T> clazz) {
    return corpusProviders.findForCorpus(corpus, clazz);
  }

  protected <T extends DataProvider> Flux<T> getProvidersFromContext(
      final AbstractGraphQLNodeSupport<?> node,
      final Class<T> clazz) {
    if (node.getGqlNode() != null) {
      final GraphQLNode gqlNode = node.getGqlNode();
      final Optional<BaleenCorpus> corpus = gqlNode.findParent(BaleenCorpus.class);
      if (corpus.isPresent()) {
        return corpusProviders.findForCorpus(corpus.get(), clazz);
      }
    }
    return Flux.empty();
  }
}
