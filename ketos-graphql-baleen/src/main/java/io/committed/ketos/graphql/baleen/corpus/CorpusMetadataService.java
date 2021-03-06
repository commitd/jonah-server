package io.committed.ketos.graphql.baleen.corpus;

import java.util.Optional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TermCount;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenCorpusMetadata;
import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.common.utils.BinUtils;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;

/** Metadata resolvers which enhance the Corpus object. */
@GraphQLService
public class CorpusMetadataService extends AbstractGraphQlService {

  protected CorpusMetadataService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "metadata", description = "Get summary of metadata in corpus")
  public BaleenCorpusMetadata getMetadata(
      @GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "key", description = "Filter to just this key") final String key) {
    return new BaleenCorpusMetadata(corpus, Optional.ofNullable(key));
  }

  @GraphQLQuery(name = "keys", description = "Get information on metadata keys")
  public Mono<TermCount> getMetadataKey(
      @GraphQLContext final BaleenCorpusMetadata corpusMetadata,
      @GraphQLArgument(
            name = "size",
            description = "Maximum number of values to return",
            defaultValue = "10"
          )
          final int size,
      @GraphQLArgument(
            name = "hints",
            description =
                "Provide hints about the datasource or database which should be used to execute this query"
          )
          final DataHints hints) {

    final Flux<TermBin> flux =
        getProvidersFromContext(corpusMetadata, MetadataProvider.class, hints)
            .flatMap(p -> p.countByKey(corpusMetadata.getKey(), size));

    return BinUtils.joinTermBins(flux);
  }

  @GraphQLQuery(name = "values", description = "Get all values for metadata")
  public Mono<TermCount> getValues(
      @GraphQLContext final BaleenCorpusMetadata corpusMetadata,
      @GraphQLArgument(
            name = "size",
            description = "Maximum number of values to return",
            defaultValue = "10"
          )
          final int size,
      @GraphQLArgument(
            name = "hints",
            description =
                "Provide hints about the datasource or database which should be used to execute this query"
          )
          final DataHints hints) {

    final Flux<TermBin> flux =
        getProvidersFromContext(corpusMetadata, MetadataProvider.class, hints)
            .flatMap(p -> p.countByValue(corpusMetadata.getKey(), size));

    return BinUtils.joinTermBins(flux);
  }
}
