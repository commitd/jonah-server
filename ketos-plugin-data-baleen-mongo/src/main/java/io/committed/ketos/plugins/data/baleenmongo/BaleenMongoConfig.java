package io.committed.ketos.plugins.data.baleenmongo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import io.committed.ketos.plugins.data.baleenmongo.repository.BaleenDocumentRepository;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DataProvider;
import io.committed.ketos.plugins.providers.services.CorpusProviders;

@Configuration
@ComponentScan(basePackageClasses = { BaleenMongoConfig.class })
@EnableReactiveMongoRepositories(basePackageClasses = { BaleenDocumentRepository.class })
public class BaleenMongoConfig {

  // TODO: Hack until we have proper corpus provider implementation
  @Bean
  public CorpusProviders corpusProviders(final List<DataProvider> providers) {
    final Map<String, List<DataProvider>> map = new HashMap<>();
    map.put("baleen", providers);
    return new CorpusProviders(map);
  }
}
