package io.committed.ketos.graphql.baleen.corpus;

import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.publisher.Flux;

import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenCorpus;

import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;

/**
 * Resolvers which enhance the Corpus object, which information about available data providers for
 * that corpus.
 */
@GraphQLService
public class CorpusProviderService {

  @Autowired private DataProviders corpusProviders;

  @GraphQLQuery(
    name = "providers",
    description = "Access the data providesr available for this corpus"
  )
  public Flux<DataProvider> providers(@GraphQLContext final BaleenCorpus corpus) {
    // TODO: Probably this should be a dto, just incase tehre's any sensitive information
    return corpusProviders.findForDataset(corpus.getId());
  }
}
