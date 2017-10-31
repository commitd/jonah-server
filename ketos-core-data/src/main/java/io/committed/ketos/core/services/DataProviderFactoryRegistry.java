package io.committed.ketos.core.services;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.committed.ketos.common.dataproviders.configuration.DataProviderFactory;
import io.committed.ketos.common.providers.baleen.DataProvider;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DataProviderFactoryRegistry {

  private final List<DataProviderFactory<?>> factories;

  @Autowired
  public DataProviderFactoryRegistry(final List<DataProviderFactory<?>> factories) {
    this.factories = factories;
  }

  public Flux<DataProviderFactory<? extends DataProvider>> findFactories(final String id) {
    return Flux.fromIterable(factories)
        .filter(f -> f.getId().equalsIgnoreCase(id));
  }

  // This is checked in the flux
  @SuppressWarnings("unchecked")
  public <P extends DataProvider> Flux<DataProviderFactory<P>> findFactories(final String id,
      final Class<P> clazz) {
    return findFactories(id)
        .filter(f -> clazz.isAssignableFrom(f.getDataProvider()))
        .map(f -> (DataProviderFactory<P>) f);
  }

  public Mono<? extends DataProvider> build(final String id, final String corpus,
      final Map<String, Object> settings) {

    final Map<String, Object> safeSettings = settings == null ? Collections.emptyMap() : settings;

    return findFactories(id)
        .flatMap(f -> {
          try {
            return f.build(corpus, safeSettings);
          } catch (final Exception e) {
            log.warn("Unable to create data provider due to error", e);
            return Mono.empty();
          }
        })
        // Grab the first non empty
        .next();
  }

}
