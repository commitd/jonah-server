package io.committed.ketos.graphql.baleen.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentSearch;
import io.committed.ketos.common.data.BaleenDocuments;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.vessel.core.dto.analytic.TermBin;
import io.committed.vessel.core.dto.analytic.TermCount;
import io.committed.vessel.core.dto.analytic.TimeBin;
import io.committed.vessel.core.dto.analytic.Timeline;
import io.committed.vessel.core.dto.constants.TimeInterval;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.committed.vessel.server.data.query.DataHints;
import io.committed.vessel.server.data.services.DatasetProviders;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@VesselGraphQlService
public class DocumentsService extends AbstractGraphQlService {

  @Autowired
  public DocumentsService(final DatasetProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "document", description = "Get document by id")
  public Mono<BaleenDocument> getDocument(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLNonNull @GraphQLArgument(name = "id", description = "Document id") final String id,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return getProviders(corpus, DocumentProvider.class, hints)
        .flatMap(p -> p.getById(id))
        .map(this.addContext(corpus))
        .next();
  }

  @GraphQLQuery(name = "searchDocuments", description = "Search for documents by query")
  public BaleenDocumentSearch getDocuments(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLNonNull @GraphQLArgument(name = "search",
          description = "Search query") final String search,
      @GraphQLArgument(name = "offset",
          description = "Index of first document to return, for pagination",
          defaultValue = "0") final int offset,
      @GraphQLArgument(name = "size",
          description = "Maximum number of documents to return, for pagination",
          defaultValue = "10") final int size) {

    return new BaleenDocumentSearch(search, offset, size)
        .addNodeContext(corpus);
  }

  @GraphQLQuery(name = "sampleDocuments", description = "Return a selection of documents")
  public BaleenDocuments getDocuments(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "offset",
          description = "Index of first document to return, for pagination",
          defaultValue = "0") final int offset,
      @GraphQLArgument(name = "size",
          description = "Maximum number of documents to return, for pagination",
          defaultValue = "10") final int size,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final Flux<DocumentProvider> providers = getProviders(corpus, DocumentProvider.class, hints);

    final Flux<BaleenDocument> documents = providers
        .flatMap(p -> p.all(offset, size))
        .map(addContext(corpus));

    final Mono<Long> count = providers.flatMap(DocumentProvider::count).reduce(0L, Long::sum);

    return new BaleenDocuments(
        documents,
        count)
            .addNodeContext(corpus);
  }


  @GraphQLQuery(name = "hits", description = "Get search hits")
  public BaleenDocuments getDocuments(@GraphQLContext final BaleenDocumentSearch documentSearch,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    // TODO; Should limit be here or on the above??

    final Optional<BaleenCorpus> corpus =
        documentSearch.getGqlNode().findParent(BaleenCorpus.class);

    if (!corpus.isPresent()) {
      return null;
    }

    final Flux<DocumentProvider> providers =
        getProviders(corpus.get(), DocumentProvider.class, hints);

    final Flux<BaleenDocument> documents = providers
        .flatMap(p -> p.search(documentSearch.getQuery(), documentSearch.getOffset(),
            documentSearch.getSize()))
        .map(addContext(corpus));

    final Mono<Long> count =
        providers.flatMap(p -> p.countSearchMatches(documentSearch.getQuery()))
            .reduce(0L, Long::sum);

    return new BaleenDocuments(documents, count).addNodeContext(documentSearch);
  }

  @GraphQLQuery(name = "documentCount", description = "Get the number of documents")
  public Mono<Long> getDocuments(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, DocumentProvider.class, hints)
        .flatMap(DocumentProvider::count)
        .reduce(0L, Long::sum);
  }

  @GraphQLQuery(name = "documentTypes", description = "Count of documents per document type")
  public Mono<TermCount> getDocumentTypes(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, DocumentProvider.class, hints)
        .flatMap(DocumentProvider::countByType)
        .groupBy(TermBin::getTerm)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TermBin(g.key(), l)))
        .collectList()
        .map(TermCount::new);
  }

  @GraphQLQuery(name = "documentLanguages", description = "Count of documents per language")
  public Mono<TermCount> getDocumentLanguages(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, DocumentProvider.class, hints)
        .flatMap(DocumentProvider::countByLanguage)
        .groupBy(TermBin::getTerm)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TermBin(g.key(), l)))
        .collectList()
        .map(TermCount::new);
  }

  @GraphQLQuery(name = "documentClassifications",
      description = "Count of documents per classification")
  public Mono<TermCount> getDocumentClassification(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, DocumentProvider.class, hints)
        .flatMap(DocumentProvider::countByClassification)
        .groupBy(TermBin::getTerm)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TermBin(g.key(), l)))
        .collectList()
        .map(TermCount::new);
  }

  @GraphQLQuery(name = "documentTimeline", description = "Timeline of documents per day")
  public Mono<Timeline> getDocumentTimeline(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, DocumentProvider.class, hints)
        .flatMap(DocumentProvider::countByDate)
        .groupBy(TimeBin::getTs)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TimeBin(g.key(), l)))
        .collectList()
        .map(l -> new Timeline(TimeInterval.DAY, l));
  }
}
