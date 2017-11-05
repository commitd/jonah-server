package io.committed.ketos.graphql.baleen.services;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.committed.vessel.server.data.dataset.Dataset;
import io.committed.vessel.server.data.services.DatasetRegistry;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@VesselGraphQlService
public class CorpusService {

  private final DatasetRegistry corpusRegistry;

  @Autowired
  public CorpusService(final DatasetRegistry corpusRegistry) {
    this.corpusRegistry = corpusRegistry;

  }

  @GraphQLQuery(name = "corpus")
  public Mono<BaleenCorpus> corpus(@GraphQLNonNull @GraphQLArgument(name = "id") final String id) {
    return corpusRegistry.findById(id).map(this::toBaleenCorpus);
  }

  @GraphQLQuery(name = "corpora")
  public Flux<BaleenCorpus> corpora() {
    return corpusRegistry.getDatasets().map(this::toBaleenCorpus);
  }

  private BaleenCorpus toBaleenCorpus(final Dataset corpus) {
    return new BaleenCorpus(corpus.getId(), corpus.getName(), corpus.getDescription());
  }
}
