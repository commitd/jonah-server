package io.committed.ketos.graphql.baleen.documentsearch;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.core.dto.analytic.TermCount;
import io.committed.invest.core.dto.analytic.Timeline;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocumentSearch;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Mono;

@GraphQLService
public class DocumentSearchDocumentsService extends AbstractGraphQlService {

  private final io.committed.ketos.graphql.baleen.corpus.CorpusDocumentsService corpusDocumentService;

  @Autowired
  public DocumentSearchDocumentsService(final DataProviders corpusProviders,
      final io.committed.ketos.graphql.baleen.corpus.CorpusDocumentsService corpusDocumentService) {
    super(corpusProviders);
    this.corpusDocumentService = corpusDocumentService;
  }


  @GraphQLQuery(name = "countDocuments", description = "Count of documents by value")
  public Mono<TermCount> getDocumentTypes(@GraphQLContext final BaleenDocumentSearch search,
      @GraphQLArgument(name = "query",
          description = "Search query") final DocumentFilter documentFilter,
      @GraphQLNonNull @GraphQLArgument(name = "field",
          description = "Provide hints about the datasource or database which should be used to execute this query") final String field,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final Optional<BaleenCorpus> corpus = search.findParent(BaleenCorpus.class);
    if (!corpus.isPresent()) {
      return Mono.empty();
    }

    return corpusDocumentService.getDocumentTypes(corpus.get(), documentFilter, field, hints);
  }

  @GraphQLQuery(name = "documentTimeline", description = "Timeline of documents per day")
  public Mono<Timeline> getDocumentTimeline(@GraphQLContext final BaleenDocumentSearch search,
      @GraphQLArgument(name = "query",
          description = "Search query") final DocumentFilter documentFilter,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final Optional<BaleenCorpus> corpus = search.findParent(BaleenCorpus.class);
    if (!corpus.isPresent()) {
      return Mono.empty();
    }
    return corpusDocumentService.getDocumentTimeline(corpus.get(), documentFilter, hints);

  }


}
