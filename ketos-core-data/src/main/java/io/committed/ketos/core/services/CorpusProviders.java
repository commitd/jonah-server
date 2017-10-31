package io.committed.ketos.core.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;

import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.providers.baleen.DataProvider;
import reactor.core.publisher.Flux;

public class CorpusProviders {

  public static final List<DataProvider> EMPTY_PROVIDER_LIST = Collections.emptyList();
  private final ImmutableListMultimap<String, DataProvider> providers;

  @Autowired
  public CorpusProviders(final List<DataProvider> providers) {
    this.providers = Multimaps.index(providers, DataProvider::getCorpus);
  }

  public Flux<DataProvider> findForCorpus(final BaleenCorpus corpus) {
    final ImmutableList<DataProvider> list = providers.get(corpus.getId());
    if (list == null || list.isEmpty()) {
      return Flux.empty();
    } else {
      return Flux.fromIterable(list);
    }
  }

  // It is checked...
  @SuppressWarnings("unchecked")
  public <T> Flux<T> findForCorpus(final BaleenCorpus corpus,
      final Class<T> providerClass) {
    return (Flux<T>) findForCorpus(corpus).filter(providerClass::isInstance);

  }

}

