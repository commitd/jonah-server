package io.committed.ketos.common.dataproviders.configuration;

import java.util.Map;

import io.committed.ketos.common.providers.baleen.DataProvider;
import reactor.core.publisher.Mono;

public interface DataProviderFactory<T extends DataProvider> {

  String getId();

  Mono<T> build(String corpus, Map<String, Object> settings);

  Class<T> getDataProvider();

  String getDatasource();

}
