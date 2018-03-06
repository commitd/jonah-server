package io.committed.ketos.graphql.baleen.corpus;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.core.dto.analytic.TermCount;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.analytic.TimeRange;
import io.committed.invest.core.dto.analytic.Timeline;
import io.committed.invest.core.dto.constants.TimeInterval;
import io.committed.invest.core.utils.FieldUtils;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.constants.ItemTypes;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.general.NamedGeoLocation;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentProbe;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.output.DocumentSearch;
import io.committed.ketos.common.graphql.output.Documents;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.common.utils.BinUtils;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@GraphQLService
public class CorpusDocumentsService extends AbstractGraphQlService {

  @Autowired
  public CorpusDocumentsService(final DataProviders corpusProviders) {
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

  @GraphQLQuery(name = "documents", description = "Get document by example")
  public Flux<BaleenDocument> getDocumentByExample(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "probe", description = "Document by example") final DocumentProbe probe,
      @GraphQLArgument(name = "offset",
          description = "Index of first document to return, for pagination",
          defaultValue = "0") final int offset,
      @GraphQLArgument(name = "size",
          description = "Maximum number of documents to return, for pagination",
          defaultValue = "10") final int size,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final Flux<DocumentProvider> providers = getProviders(corpus, DocumentProvider.class, hints);

    Flux<BaleenDocument> documents;
    if (probe != null) {
      documents = providers.flatMap(p -> p.getByExample(probe, offset, size));
    } else {
      documents = providers.flatMap(p -> p.getAll(offset, size));

    }

    return documents.doOnNext(eachAddParent(corpus));
  }

  @GraphQLQuery(name = "sampleDocuments", description = "Return a selection of documents")
  public Documents getDocuments(@GraphQLContext final BaleenCorpus corpus,
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
        providers.flatMap(p -> p.getAll(offset, size));

    final Mono<Long> count = providers.flatMap(DocumentProvider::count).reduce(Long::sum);

    return Documents.builder().parent(corpus).results(documents).total(count).offset(offset).size(size).build();
  }

  @GraphQLQuery(name = "countDocuments", description = "Get the number of documents")
  public Mono<Long> getDocuments(@GraphQLContext final BaleenCorpus corpus, @GraphQLArgument(
      name = "hints",
      description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, DocumentProvider.class, hints).flatMap(DocumentProvider::count)
        .reduce(Long::sum);
  }

  @GraphQLQuery(name = "searchDocuments", description = "Search for documents by query")
  public DocumentSearch getDocuments(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLNonNull @GraphQLArgument(name = "query",
          description = "Search query") final DocumentFilter documentFilter,
      @GraphQLArgument(name = "mentions",
          description = "Including mentions") final List<MentionFilter> mentionFilters,
      @GraphQLArgument(name = "entities",
          description = "Including entities") final List<EntityFilter> entityFilters,
      @GraphQLArgument(name = "relations",
          description = "Include relations") final List<RelationFilter> relationFilters) {

    return DocumentSearch.builder()
        .parent(corpus)
        .documentFilter(documentFilter)
        .mentionFilters(mentionFilters)
        .relationFilters(relationFilters)
        .entityFilters(entityFilters)
        .build();
  }

  @GraphQLQuery(name = "countByDocumentField", description = "Count of documents by value")
  public Mono<TermCount> getDocumentTypes(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "query",
          description = "Search query") final DocumentFilter documentFilter,
      @GraphQLNonNull @GraphQLArgument(name = "field",
          description = "Provide hints about the datasource or database which should be used to execute this query") final String field,
      @GraphQLArgument(name = "size",
          description = "Maximum number of documents to return, for pagination",
          defaultValue = "10") final int size,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final List<String> path = FieldUtils.fieldSplitter(field);

    if (path.isEmpty()) {
      return Mono.empty();
    }

    return BinUtils.joinTermBins(getProviders(corpus, DocumentProvider.class, hints)
        .flatMap(p -> p.countByField(Optional.ofNullable(documentFilter), path, size)));
  }

  @GraphQLQuery(name = "documentTimeline", description = "Timeline of documents per day")
  public Mono<Timeline> getDocumentTimeline(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "query",
          description = "Search query") final DocumentFilter documentFilter,
      @GraphQLArgument(name = "interval",
          description = "Time interval to group by") final TimeInterval interval,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    // If intervalis null, calculate one
    TimeInterval bestInterval;
    if (interval == null) {
      bestInterval = documentTimeRange(corpus, documentFilter, hints).block().getInterval();
    } else {
      bestInterval = interval;
    }

    return getProviders(corpus, DocumentProvider.class, hints)
        .flatMap(p -> p.countByDate(Optional.ofNullable(documentFilter), bestInterval))
        .groupBy(TimeBin::getTs)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount())
            .map(l -> new TimeBin(g.key(), l)))
        .collectList()
        .map(l -> new Timeline(bestInterval, l));
  }


  // TODO: This could also exist on the documents search but it would raise issues what what we do
  // with the (entity,relation,metnion) filter on that search. It seems like quite a lot of work to
  // make it consistent with the document search)
  @GraphQLQuery(name = "countByTypesField",
      description = "Count of type (entity,relation,mention) by field value including a document filter")
  public Mono<TermCount> getDocumentTypes(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "query",
          description = "Search query") final DocumentFilter documentFilter,
      @GraphQLNonNull @GraphQLArgument(name = "field",
          description = "Provide hints about the datasource or database which should be used to execute this query") final String field,
      @GraphQLNonNull @GraphQLArgument(name = "type",
          description = "The type (entity,relation,mention) to join") final ItemTypes type,
      @GraphQLArgument(name = "size",
          description = "Maximum number of documents to return, for pagination",
          defaultValue = "10") final int size,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final List<String> path = FieldUtils.fieldSplitter(field);

    if (path.isEmpty()) {
      return Mono.empty();
    }

    final Flux<DocumentProvider> providers = getProviders(corpus, DocumentProvider.class, hints);
    return BinUtils.joinTermBins(providers
        .flatMap(p -> p.countByJoinedField(Optional.ofNullable(documentFilter), type, path, size)));
  }

  @GraphQLQuery(name = "timelineByTypeField",
      description = "Count of type (entity,mention) by field value including a document filter")
  public Mono<Timeline> countByTypeDates(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "query",
          description = "Search query") final DocumentFilter documentFilter,
      @GraphQLNonNull @GraphQLArgument(name = "type",
          description = "The type (entity,mention) to join") final ItemTypes type,
      @GraphQLArgument(name = "interval",
          description = "Time interval to group by") final TimeInterval interval,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {


    // If intervalis null, calculate one
    TimeInterval bestInterval;
    if (interval == null) {
      // If we have a entity or a mention calculate based on entities (should basically be the same)
      // Else (which probably isn't going to happen much but...) use document
      if (type == ItemTypes.ENTITY || type == ItemTypes.MENTION) {
        bestInterval = entityTimeRange(corpus, documentFilter, hints).block().getInterval();
      } else {
        bestInterval = documentTimeRange(corpus, documentFilter, hints).block().getInterval();
      }

    } else {
      bestInterval = interval;
    }

    final Flux<DocumentProvider> providers = getProviders(corpus, DocumentProvider.class, hints);
    return BinUtils.joinTimeBins(
        providers.flatMap(p -> p.countByJoinedDate(Optional.ofNullable(documentFilter), type, bestInterval)),
        bestInterval);
  }

  @GraphQLQuery(name = "documentLocations",
      description = "Count of type (entity,mention) by field value including a document filter")
  public Flux<NamedGeoLocation> documentLocations(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "query",
          description = "Search query") final DocumentFilter documentFilter,
      @GraphQLArgument(name = "size",
          description = "Maximum number of locations to return",
          defaultValue = "10") final int size,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final Flux<DocumentProvider> providers = getProviders(corpus, DocumentProvider.class, hints);
    return providers.flatMap(p -> p.documentLocations(Optional.ofNullable(documentFilter), size));
  }

  @GraphQLQuery(name = "documentTimeRange", description = "Get the range of dates for entities")
  public Mono<TimeRange> documentTimeRange(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "query",
          description = "Search query") final DocumentFilter documentFilter,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, DocumentProvider.class, hints)
        .flatMap(p -> p.documentTimeRange(Optional.ofNullable(documentFilter)))
        .reduce(new TimeRange(), (acc, r) -> {
          acc.expand(r);
          return acc;
        });
  }


  @GraphQLQuery(name = "entityTimeRange", description = "Get the range of dates for entities")
  public Mono<TimeRange> entityTimeRange(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "query",
          description = "Search query") final DocumentFilter documentFilter,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, DocumentProvider.class, hints)
        .flatMap(p -> p.entityTimeRange(Optional.ofNullable(documentFilter)))
        .reduce(new TimeRange(), (acc, r) -> {
          acc.expand(r);
          return acc;
        });
  }
}
