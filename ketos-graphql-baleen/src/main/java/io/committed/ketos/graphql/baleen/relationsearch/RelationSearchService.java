package io.committed.ketos.graphql.baleen.relationsearch;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.graphql.intermediate.RelationSearchResult;
import io.committed.ketos.common.graphql.output.RelationSearch;
import io.committed.ketos.common.graphql.output.Relations;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;

/** Core relation search functionality. */
@GraphQLService
public class RelationSearchService extends AbstractGraphQlService {
  @Autowired
  public RelationSearchService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "hits", description = "Get search hits")
  public Relations getDocuments(
      @GraphQLContext final RelationSearch search,
      @GraphQLArgument(
            name = "offset",
            description = "Index of first document to return, for pagination",
            defaultValue = "0"
          )
          final int offset,
      @GraphQLArgument(
            name = "size",
            description = "Maximum number of documents to return, for pagination",
            defaultValue = "10"
          )
          final int size,
      @GraphQLArgument(
            name = "hints",
            description =
                "Provide hints about the datasource or database which should be used to execute this query"
          )
          final DataHints hints) {

    final Optional<BaleenCorpus> optionalCorpus = search.findParent(BaleenCorpus.class);

    if (!optionalCorpus.isPresent()) {
      return null;
    }

    final Flux<RelationSearchResult> results =
        getProviders(optionalCorpus.get(), RelationProvider.class, hints)
            .map(p -> p.search(search, offset, size));

    final Mono<Long> count = results.flatMap(RelationSearchResult::getTotal).reduce(Long::sum);
    final Flux<BaleenRelation> flux = Flux.concat(results.map(RelationSearchResult::getResults));

    return Relations.builder()
        .parent(search)
        .results(flux)
        .total(count)
        .offset(offset)
        .size(size)
        .build();
  }
}
