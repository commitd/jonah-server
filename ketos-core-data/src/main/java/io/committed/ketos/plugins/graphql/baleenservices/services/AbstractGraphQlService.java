package io.committed.ketos.plugins.graphql.baleenservices.services;

import java.util.Optional;
import java.util.function.Function;

import io.committed.graphql.support.AbstractGraphQLNodeSupport;
import io.committed.graphql.support.GraphQLNode;
import io.committed.ketos.plugins.data.baleen.BaleenCorpus;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DataProvider;
import io.committed.ketos.plugins.providers.services.CorpusProviders;
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
