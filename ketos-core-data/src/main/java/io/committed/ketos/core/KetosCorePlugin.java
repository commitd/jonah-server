package io.committed.ketos.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.committed.ketos.common.dataproviders.configuration.DataProviderFactory;
import io.committed.ketos.common.providers.baleen.DataProvider;
import io.committed.ketos.core.configuration.Corpus;
import io.committed.ketos.core.configuration.DataProviderSpecification;
import io.committed.ketos.core.services.CorpusDataProviderCreationService;
import io.committed.ketos.core.services.CorpusProviders;
import io.committed.ketos.core.services.CorpusRegistry;
import io.committed.ketos.core.services.DataProviderFactoryRegistry;
import io.committed.vessel.extensions.VesselServiceExtension;

public class KetosCorePlugin implements VesselServiceExtension {

  @Override
  public Class<?> getConfiguration() {
    return PluginConfiguration.class;
  }


  @Configuration
  @ComponentScan(basePackageClasses = PluginConfiguration.class)
  public static class PluginConfiguration {


    @Bean
    public CorpusProviders corpusProviders(
        @Autowired(required = false) final List<DataProvider> providers) {
      return new CorpusProviders(providers == null ? Collections.emptyList() : providers);
    }

    @Bean
    public CorpusRegistry corpusRegistry(final List<Corpus> corpora) {
      return new CorpusRegistry(corpora);
    }

    @Bean
    public DataProviderFactoryRegistry dataProviderFactoryRegistry(
        final List<DataProviderFactory<?>> factories) {
      return new DataProviderFactoryRegistry(factories);
    }

    @Bean
    public CorpusDataProviderCreationService corpusDataProviderCreationService(
        final CorpusRegistry corpusRegistry,
        final DataProviderFactoryRegistry dataProviderFactoryRegistry) {
      return new CorpusDataProviderCreationService(corpusRegistry, dataProviderFactoryRegistry);
    }

    // TODO; Should be provided by configuration

    @Bean
    public Corpus baleenCorpus() {
      final Corpus c = new Corpus();
      c.setId("baleen");
      c.setDescription("Baleen default corpus");
      c.setName("Baleen");
      c.setProviders(Arrays.asList(
          DataProviderSpecification.builder().factory("baleen-mongo-documents")
              .build(),
          DataProviderSpecification.builder().factory("baleen-mongo-mentions")
              .build(),
          DataProviderSpecification.builder().factory("baleen-mongo-entities")
              .build(),
          DataProviderSpecification.builder().factory("baleen-mongo-relations")
              .build()));
      return c;
    }


  }
}
