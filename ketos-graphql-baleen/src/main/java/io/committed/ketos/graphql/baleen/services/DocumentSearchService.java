package io.committed.ketos.graphql.baleen.services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentSearch;
import io.committed.ketos.common.data.BaleenDocuments;
import io.committed.ketos.common.graphql.input.DocumentSearchResult;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@GraphQLService
public class DocumentSearchService extends AbstractGraphQlService {
  @Autowired
  public DocumentSearchService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "hits", description = "Get search hits")
  public BaleenDocuments getDocuments(@GraphQLContext final BaleenDocumentSearch documentSearch,
      @GraphQLArgument(name = "offset",
          description = "Index of first document to return, for pagination",
          defaultValue = "0") final int offset,
      @GraphQLArgument(name = "size",
          description = "Maximum number of documents to return, for pagination",
          defaultValue = "10") final int size,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final Optional<BaleenCorpus> optionalCorpus =
        documentSearch.findParent(BaleenCorpus.class);

    if (!optionalCorpus.isPresent()) {
      return null;
    }

    final BaleenCorpus corpus = optionalCorpus.get();

    final Flux<DocumentProvider> providers = getProviders(corpus, DocumentProvider.class, hints);

    final Flux<DocumentSearchResult> results =
        providers.map(p -> p.search(documentSearch, offset, size));

    final Mono<Long> count = results.reduce(0L, (a, r) -> a + r.getTotal());
    final Flux<BaleenDocument> documents = Flux.concat(results.map(DocumentSearchResult::getResults));

    return BaleenDocuments.builder()
        .parent(documentSearch)
        .results(documents)
        .total(count)
        .build();
  }
}
