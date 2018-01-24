package io.committed.ketos.graphql.baleen.services;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TermCount;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.analytic.Timeline;
import io.committed.invest.core.dto.constants.TimeInterval;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentSearch;
import io.committed.ketos.common.data.BaleenDocuments;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentProbe;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.graphql.baleen.utils.BaleenUtils;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@GraphQLService
public class DocumentsService extends AbstractGraphQlService {

  @Autowired
  public DocumentsService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "document", description = "Get document by id")
  public Mono<BaleenDocument> getDocument(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLNonNull @GraphQLArgument(name = "id", description = "Document id") final String id,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return getProviders(corpus, DocumentProvider.class, hints)
        .flatMap(p -> p.getById(id))
        .next()
        .doOnNext(eachAddParent(corpus));
  }

  @GraphQLQuery(name = "documentByExample", description = "Get document by example")
  public Flux<BaleenDocument> getDocumentByExample(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLNonNull @GraphQLArgument(name = "probe", description = "Document by example") final DocumentProbe probe,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return getProviders(corpus, DocumentProvider.class, hints)
        .flatMap(p -> p.getByExample(probe))
        .doOnNext(eachAddParent(corpus));
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

    final Flux<BaleenDocument> documents =
        providers.flatMap(p -> p.all(offset, size));

    final Mono<Long> count = providers.flatMap(DocumentProvider::count).reduce(0L, Long::sum);

    return BaleenDocuments.builder().parent(corpus).results(documents).total(count).build();
  }

  @GraphQLQuery(name = "documentCount", description = "Get the number of documents")
  public Mono<Long> getDocuments(@GraphQLContext final BaleenCorpus corpus, @GraphQLArgument(
      name = "hints",
      description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, DocumentProvider.class, hints).flatMap(DocumentProvider::count)
        .reduce(0L, Long::sum);
  }

  @GraphQLQuery(name = "documents", description = "Search for documents by query")
  public BaleenDocumentSearch getDocuments(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLNonNull @GraphQLArgument(name = "query",
          description = "Search query") final DocumentFilter documentFilter,
      @GraphQLNonNull @GraphQLArgument(name = "mentions",
          description = "Including mentions") final List<MentionFilter> mentionFilters,
      @GraphQLNonNull @GraphQLArgument(name = "relations",
          description = "Include relations") final List<RelationFilter> relationFilters) {

    return BaleenDocumentSearch.builder()
        .parent(corpus)
        .documentFilter(documentFilter)
        .mentionFilters(mentionFilters)
        .relationFilters(relationFilters)
        .build();
  }

  @GraphQLQuery(name = "countDocuments", description = "Count of documents by value")
  public Mono<TermCount> getDocumentTypes(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLNonNull @GraphQLArgument(name = "field",
          description = "Provide hints about the datasource or database which should be used to execute this query") final String field,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final List<String> path = BaleenUtils.fieldSplitter(field);

    if (path.isEmpty()) {
      return Mono.empty();
    }

    return getProviders(corpus, DocumentProvider.class, hints)
        .flatMap(p -> p.countByField(path))
        .groupBy(TermBin::getTerm)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TermBin(g.key(), l)))
        .collectList().map(TermCount::new);
  }

  @GraphQLQuery(name = "documentTimeline", description = "Timeline of documents per day")
  public Mono<Timeline> getDocumentTimeline(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, DocumentProvider.class, hints)
        .flatMap(DocumentProvider::countByDate).groupBy(TimeBin::getTs)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TimeBin(g.key(), l)))
        .collectList().map(l -> new Timeline(TimeInterval.DAY, l));
  }

  @GraphQLQuery(name = "document", description = "Document containing the entity")
  public Mono<BaleenDocument> getDocumentForEntity(@GraphQLContext final BaleenEntity entity,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final Optional<BaleenCorpus> corpus = entity.findParent(BaleenCorpus.class);

    if (!corpus.isPresent()) {
      return null;
    }

    return getDocument(corpus.get(), entity.getDocId(), hints)
        .doOnNext(eachAddParent(entity));
  }

}
