package io.committed.ketos.plugins.providers.services;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import io.committed.ketos.plugins.data.configurer.DataProviderFactoryRegistry;
import io.committed.ketos.plugins.data.configurer.DataProviderSpecification;
import io.committed.ketos.plugins.data.corupus.Corpus;
import io.committed.ketos.plugins.data.corupus.CorpusRegistry;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DataProvider;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DocumentProvider;
import io.committed.ketos.plugins.graphql.baleenservices.providers.EntityProvider;
import io.committed.ketos.plugins.graphql.baleenservices.providers.MentionProvider;
import io.committed.ketos.plugins.graphql.baleenservices.providers.RelationProvider;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class CorpusDataProviderCreationService implements BeanFactoryPostProcessor {

  private final CorpusRegistry corpusRegistry;
  private final DataProviderFactoryRegistry dataProviderFactoryRegistry;

  @Autowired
  public CorpusDataProviderCreationService(final CorpusRegistry corpusRegistry,
      final DataProviderFactoryRegistry dataProviderFactoryRegistry) {
    this.corpusRegistry = corpusRegistry;
    this.dataProviderFactoryRegistry = dataProviderFactoryRegistry;
  }

  @Override
  public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    corpusRegistry.getCorpora()
        .doOnNext(c -> register(beanFactory, c))
        .subscribe();

  }

  private Corpus register(final ConfigurableListableBeanFactory beanFactory, final Corpus corpus) {
    corpus.getProviders().forEach(s -> register(beanFactory, corpus, s));
    return corpus;
  }

  private DataProvider register(final ConfigurableListableBeanFactory beanFactory,
      final Corpus corpus,
      final DataProviderSpecification spec) {

    final Mono<? extends DataProvider> mono =
        dataProviderFactoryRegistry.build(spec.getFactory(),
            getProviderClass(spec.getProvider()),
            corpus.getId(),
            spec.getSettings());

    final DataProvider provider = mono.block();

    if (provider != null) {
      beanFactory.registerSingleton(makeId(spec, corpus), provider);

    } else {
      log.error("Unable to create data provider from specifrication for corpus {}, {}",
          corpus.getId(), spec);
    }

    return provider;
  }

  private String makeId(final DataProviderSpecification spec, final Corpus corpus) {
    return String.format("dp-%s-%s-%s", corpus.getId(), spec.getFactory(), spec.getProvider());
  }

  private Class<? extends DataProvider> getProviderClass(final String provider) {
    // TODO this needs to be extensibly.. so does that me we need a dataprovider extension??

    switch (provider) {
      case "DocumentProvider":
        return DocumentProvider.class;
      case "EntityProvider":
        return EntityProvider.class;
      case "RelationProvider":
        return RelationProvider.class;
      case "MentionProvider":
        return MentionProvider.class;
      default:
        throw new RuntimeException("Unknown data provider");
    }
  }
}
