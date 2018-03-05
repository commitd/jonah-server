package io.committed.ketos.graphql.baleen.entitysearch;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.intermediate.EntitySearchResult;
import io.committed.ketos.common.graphql.output.Entities;
import io.committed.ketos.common.graphql.output.EntitySearch;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@GraphQLService
public class EntitySearchService extends AbstractGraphQlService {
  @Autowired
  public EntitySearchService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "hits", description = "Get search hits")
  public Entities getDocuments(@GraphQLContext final EntitySearch search,
      @GraphQLArgument(name = "offset",
          description = "Index of first document to return, for pagination",
          defaultValue = "0") final int offset,
      @GraphQLArgument(name = "size",
          description = "Maximum number of documents to return, for pagination",
          defaultValue = "10") final int size,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final Optional<BaleenCorpus> optionalCorpus =
        search.findParent(BaleenCorpus.class);

    if (!optionalCorpus.isPresent()) {
      return null;
    }

    final BaleenCorpus corpus = optionalCorpus.get();

    final Flux<EntityProvider> providers = getProviders(corpus, EntityProvider.class, hints);

    final Flux<EntitySearchResult> results =
        providers.map(p -> p.search(search, offset, size));

    final Mono<Long> count = results.flatMap(EntitySearchResult::getTotal).reduce(Long::sum);
    final Flux<BaleenEntity> entities = Flux.concat(results.map(EntitySearchResult::getResults));

    return Entities.builder()
        .parent(search)
        .results(entities)
        .total(count)
        .offset(offset)
        .size(size)
        .build();
  }
}
