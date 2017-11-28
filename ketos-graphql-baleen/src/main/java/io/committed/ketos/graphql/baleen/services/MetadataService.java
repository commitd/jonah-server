package io.committed.ketos.graphql.baleen.services;

import java.util.Optional;

import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenCorpusMetadata;
import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.vessel.core.dto.analytic.TermBin;
import io.committed.vessel.core.dto.analytic.TermCount;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.committed.vessel.server.data.query.DataHints;
import io.committed.vessel.server.data.services.DatasetProviders;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@VesselGraphQlService
public class MetadataService extends AbstractGraphQlService {

  protected MetadataService(final DatasetProviders corpusProviders) {
    super(corpusProviders);
  }


  @GraphQLQuery(name = "metadata", description = "Get summary of metadata in corpus")
  public BaleenCorpusMetadata getMetadata(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "key", description = "Filter to just this key") final String key) {
    return new BaleenCorpusMetadata(Optional.ofNullable(key)).addNodeContext(corpus);
  }



  @GraphQLQuery(name = "keys", description = "Get information on metadata keys")
  public Mono<TermCount> getMetadataKey(
      @GraphQLContext final BaleenCorpusMetadata corpusMetadata,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final Flux<TermBin> flux;
    if (corpusMetadata.getKey().isPresent()) {
      flux = getProvidersFromContext(corpusMetadata, MetadataProvider.class, hints)
          .flatMap(p -> p.countByKey(corpusMetadata.getKey().get()));
    } else {
      flux = getProvidersFromContext(corpusMetadata, MetadataProvider.class, hints)
          .flatMap(MetadataProvider::countByKey);
    }

    return joinTermBins(flux);
  }

  @GraphQLQuery(name = "values", description = "Get all values for metadata")
  public Mono<TermCount> getValues(
      @GraphQLContext final BaleenCorpusMetadata corpusMetadata,
      @GraphQLArgument(name = "size", description = "Maximum number of values to return",
          defaultValue = "10") final int size,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final Flux<TermBin> flux;

    if (corpusMetadata.getKey().isPresent()) {
      flux = getProvidersFromContext(corpusMetadata, MetadataProvider.class, hints)
          .flatMap(p -> p.countByValue(corpusMetadata.getKey().get()));
    } else {
      flux = getProvidersFromContext(corpusMetadata, MetadataProvider.class, hints)
          .flatMap(MetadataProvider::countByValue);
    }

    return joinTermBins(flux);
  }

  private Mono<TermCount> joinTermBins(final Flux<TermBin> flux) {
    return flux.groupBy(TermBin::getTerm)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TermBin(g.key(), l)))
        .collectList()
        .map(TermCount::new);
  }
}
