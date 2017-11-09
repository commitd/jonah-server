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
import io.committed.vessel.server.data.services.DatasetProviders;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Mono;

@VesselGraphQlService
public class DocumentsService extends AbstractGraphQlService {

  @Autowired
  public DocumentsService(final DatasetProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "document")
  public Mono<BaleenDocument> getDocument(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLNonNull @GraphQLArgument(name = "id") final String id) {

    return getProviders(corpus, DocumentProvider.class)
        .flatMap(p -> p.getById(id))
        .map(this.addContext(corpus))
        .next();
  }

  @GraphQLQuery(name = "searchDocuments")
  public BaleenDocumentSearch getDocuments(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLNonNull @GraphQLArgument(name = "search") final String search,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit) {

    return new BaleenDocumentSearch(search, limit)
        .addNodeContext(corpus);
  }

  @GraphQLQuery(name = "sampleDocuments")
  public BaleenDocuments getDocuments(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit) {

    return new BaleenDocuments(
        getProviders(corpus, DocumentProvider.class).flatMap(p -> p.all(limit))
            .take(limit))
                .addNodeContext(corpus);
  }


  @GraphQLQuery(name = "hits")
  public BaleenDocuments getDocuments(@GraphQLContext final BaleenDocumentSearch documentSearch,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit) {

    final Optional<BaleenCorpus> corpus =
        documentSearch.getGqlNode().findParent(BaleenCorpus.class);

    if (!corpus.isPresent()) {
      return null;
    }

    return new BaleenDocuments(
        getProviders(corpus.get(), DocumentProvider.class)
            .flatMap(p -> p.search(documentSearch.getQuery(), documentSearch.getLimit())
                .map(addContext(documentSearch)))
            .take(limit))
                .addNodeContext(documentSearch);
  }

  @GraphQLQuery(name = "documentCount")
  public Mono<Long> getDocuments(@GraphQLContext final BaleenCorpus corpus) {
    return getProviders(corpus, DocumentProvider.class)
        .flatMap(DocumentProvider::count)
        .reduce(0L, Long::sum);
  }

  @GraphQLQuery(name = "documentTypes")
  public Mono<TermCount> getDocumentTypes(@GraphQLContext final BaleenCorpus corpus) {
    return getProviders(corpus, DocumentProvider.class)
        .flatMap(DocumentProvider::countByType)
        .groupBy(TermBin::getTerm)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TermBin(g.key(), l)))
        .collectList()
        .map(TermCount::new);
  }

  @GraphQLQuery(name = "documentLanguages")
  public Mono<TermCount> getDocumentLanguages(@GraphQLContext final BaleenCorpus corpus) {
    return getProviders(corpus, DocumentProvider.class)
        .flatMap(DocumentProvider::countByLanguage)
        .groupBy(TermBin::getTerm)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TermBin(g.key(), l)))
        .collectList()
        .map(TermCount::new);
  }

  @GraphQLQuery(name = "documentClassifications")
  public Mono<TermCount> getDocumentClassification(@GraphQLContext final BaleenCorpus corpus) {
    return getProviders(corpus, DocumentProvider.class)
        .flatMap(DocumentProvider::countByClassification)
        .groupBy(TermBin::getTerm)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TermBin(g.key(), l)))
        .collectList()
        .map(TermCount::new);
  }

  @GraphQLQuery(name = "documentTimeline")
  public Mono<Timeline> getDocumentTimeline(@GraphQLContext final BaleenCorpus corpus) {
    return getProviders(corpus, DocumentProvider.class)
        .flatMap(DocumentProvider::countByDate)
        .groupBy(TimeBin::getTs)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TimeBin(g.key(), l)))
        .collectList()
        .map(l -> new Timeline(TimeInterval.DAY, l));
  }
}
