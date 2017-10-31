package io.committed.ketos.plugins.graphql.baleenservices.services;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.plugins.data.baleen.BaleenCorpus;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DataProvider;
import io.committed.ketos.plugins.providers.services.CorpusProviders;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;

@VesselGraphQlService
public class ProviderService {

  @Autowired
  private CorpusProviders corpusProviders;

  @GraphQLQuery
  public Flux<DataProvider> providers(@GraphQLContext final BaleenCorpus corpus) {
    // TODO: Probably this should be a dto, just incase tehre's any sensitive information
    return corpusProviders.findForCorpus(corpus);
  }

}
