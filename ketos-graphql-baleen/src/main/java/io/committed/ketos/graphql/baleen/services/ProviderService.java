package io.committed.ketos.graphql.baleen.services;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.invest.extensions.graphql.GraphQLService;
import io.committed.invest.server.data.providers.DataProvider;
import io.committed.invest.server.data.services.DatasetProviders;
import io.committed.ketos.common.data.BaleenCorpus;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;

@GraphQLService
public class ProviderService {

  @Autowired
  private DatasetProviders corpusProviders;

  @GraphQLQuery(name = "providers",
      description = "Access the data providesr available for this corpus")
  public Flux<DataProvider> providers(@GraphQLContext final BaleenCorpus corpus) {
    // TODO: Probably this should be a dto, just incase tehre's any sensitive information
    return corpusProviders.findForDataset(corpus.getId());
  }

}
