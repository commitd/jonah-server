package io.committed.ketos.graphql.baleen.mentionsearch;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.graphql.intermediate.MentionSearchResult;
import io.committed.ketos.common.graphql.output.MentionSearch;
import io.committed.ketos.common.graphql.output.Mentions;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@GraphQLService
public class MentionSearchService extends AbstractGraphQlService {
  @Autowired
  public MentionSearchService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "hits", description = "Get search hits")
  public Mentions getDocuments(@GraphQLContext final MentionSearch search,
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


    final Flux<MentionSearchResult> results = getProviders(optionalCorpus.get(), MentionProvider.class, hints)
        .map(p -> p.search(search, offset, size));

    final Mono<Long> count = results.flatMap(MentionSearchResult::getTotal).reduce(Long::sum);
    final Flux<BaleenMention> flux = Flux.concat(results.map(MentionSearchResult::getResults));

    return Mentions.builder()
        .parent(search)
        .results(flux)
        .total(count)
        .build();
  }
}
