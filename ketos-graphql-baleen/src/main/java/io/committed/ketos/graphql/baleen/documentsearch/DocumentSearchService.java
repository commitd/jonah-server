package io.committed.ketos.graphql.baleen.documentsearch;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import io.committed.ketos.common.graphql.output.DocumentSearch;
import io.committed.ketos.common.graphql.output.Documents;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;

/** Core functionality for document search. */
@GraphQLService
public class DocumentSearchService extends AbstractGraphQlService {
  @Autowired
  public DocumentSearchService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "hits", description = "Get search hits")
  public Documents getDocuments(
      @GraphQLContext final DocumentSearch documentSearch,
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

    final Optional<BaleenCorpus> optionalCorpus = documentSearch.findParent(BaleenCorpus.class);

    if (!optionalCorpus.isPresent()) {
      return null;
    }

    final BaleenCorpus corpus = optionalCorpus.get();

    final Flux<DocumentProvider> providers = getProviders(corpus, DocumentProvider.class, hints);

    final Flux<DocumentSearchResult> results =
        providers.map(p -> p.search(documentSearch, offset, size));

    final Mono<Long> count = results.flatMap(DocumentSearchResult::getTotal).reduce(Long::sum);
    final Flux<BaleenDocument> documents =
        Flux.concat(results.map(DocumentSearchResult::getResults));

    return Documents.builder()
        .parent(documentSearch)
        .results(documents)
        .total(count)
        .offset(offset)
        .size(size)
        .build();
  }
}
