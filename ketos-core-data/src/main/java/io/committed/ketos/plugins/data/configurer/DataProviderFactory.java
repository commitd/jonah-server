package io.committed.ketos.plugins.data.configurer;

import java.util.Map;

import io.committed.ketos.plugins.graphql.baleenservices.providers.DataProvider;
import reactor.core.publisher.Mono;

public interface DataProviderFactory<T extends DataProvider> {

  String getId();

  Mono<T> build(String corpus, Map<String, Object> settings);

  Class<T> getDataProvider();

}
