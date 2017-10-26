package io.committed.ketos.plugins.providers.services;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.plugins.data.baleen.BaleenCorpus;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DataProvider;
import reactor.core.publisher.Flux;

public class CorpusProviders {

  public static final List<DataProvider> EMPTY_PROVIDER_LIST = Collections.emptyList();

  private final Map<String, List<DataProvider>> providers;

  @Autowired
  public CorpusProviders(final Map<String, List<DataProvider>> providers) {
    this.providers = providers;
  }

  public Flux<DataProvider> findForCorpus(final BaleenCorpus corpus) {
    return Flux.fromIterable(providers.getOrDefault(corpus.getId(), EMPTY_PROVIDER_LIST));
  }

  // It is checked...
  @SuppressWarnings("unchecked")
  public <T> Flux<T> findForCorpus(final BaleenCorpus corpus,
      final Class<T> providerClass) {
    return (Flux<T>) findForCorpus(corpus).filter(providerClass::isInstance);

  }

}
