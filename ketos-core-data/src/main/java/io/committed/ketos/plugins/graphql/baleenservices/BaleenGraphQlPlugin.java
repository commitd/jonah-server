package io.committed.ketos.plugins.graphql.baleenservices;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.committed.ketos.plugins.data.configurer.DataProviderFactory;
import io.committed.ketos.plugins.data.configurer.DataProviderFactoryRegistry;
import io.committed.ketos.plugins.data.corupus.Corpus;
import io.committed.ketos.plugins.data.corupus.CorpusRegistry;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DataProvider;
import io.committed.ketos.plugins.providers.services.CorpusProviders;
import io.committed.vessel.extensions.VesselGraphQlExtension;

public class BaleenGraphQlPlugin implements VesselGraphQlExtension {

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

    // TODO; Should be provided by configuration

    @Bean
    public Corpus baleenCorpus() {
      final Corpus c = new Corpus();
      c.setId("baleen");
      c.setDescription("Baleen defaul corpus");
      c.setName("Baleen");
      return c;
    }


  }
}
