
package io.committed.ketos.plugins.data.mongo.providers;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.constants.TimeInterval;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.OutputDocument;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import io.committed.ketos.common.graphql.output.DocumentSearch;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.plugins.data.mongo.filters.DocumentFilters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class MongoDocumentProvider extends AbstractBaleenMongoDataProvider<OutputDocument>
    implements DocumentProvider {

  private final String mentionCollection;
  private final String entityCollection;
  private final String relationCollection;


  public MongoDocumentProvider(final String dataset, final String datasource,
      final MongoDatabase database, final String collectionName,
      final String mentionCollection, final String entityCollection, final String relationCollection) {
    super(dataset, datasource, database, collectionName, OutputDocument.class);
    this.mentionCollection = mentionCollection;
    this.entityCollection = entityCollection;
    this.relationCollection = relationCollection;
  }

  @Override
  public Mono<BaleenDocument> getById(final String id) {
    return findByExternalId(id).map(Converters::toBaleenDocument);
  }

  @Override
  public DocumentSearchResult search(final DocumentSearch documentSearch, final int offset, final int size) {
    if (DocumentFilters.isAggregation(documentSearch)) {
      // IF we have relations / mentions we have to do an aggregation in order to do a join ($lookup).
      return searchByAggregation(documentSearch, offset, size);
    } else {
      // Otherwise life is easier and we can just query on the document
      return searchByFind(documentSearch, offset, size);
    }
  }

  @Override
  public Flux<BaleenDocument> getAll(final int offset, final int size) {
    return findAll(offset, size).map(Converters::toBaleenDocument);
  }


  private DocumentSearchResult searchByAggregation(final DocumentSearch documentSearch, final int offset,
      final int size) {

    final List<Bson> aggregation =
        DocumentFilters.createAggregation(documentSearch, getCollectionName(), mentionCollection, relationCollection);

    final Flux<BaleenDocument> results = aggregate(aggregation, OutputDocument.class)
        .skip(offset)
        .take(size)
        .map(Converters::toBaleenDocument);

    return new DocumentSearchResult(results, Mono.empty());
  }


  private DocumentSearchResult searchByFind(final DocumentSearch documentSearch, final int offset, final int size) {
    final Optional<Bson> filter = DocumentFilters.createFilter(documentSearch);

    Publisher<Long> countPublisher;
    FindPublisher<OutputDocument> findPublisher;
    if (filter.isPresent()) {
      countPublisher = getCollection().count(filter.get());
      findPublisher = getCollection().find(filter.get());
    } else {
      countPublisher = getCollection().count();
      findPublisher = getCollection().find();
    }

    final Mono<Long> total = toMono(countPublisher);
    final Flux<BaleenDocument> flux = toFlux(findPublisher)
        .skip(offset)
        .take(size)
        .map(Converters::toBaleenDocument);

    return new DocumentSearchResult(flux, total);
  }



  @Override
  public Flux<TimeBin> countByDate(final Optional<DocumentFilter> documentFilter, final TimeInterval interval) {

    final List<Bson> aggregation = new LinkedList<>();

    DocumentFilters.createFilter(documentFilter).ifPresent(f -> {
      aggregation.add(Aggregates.match(f));
    });

    String dateString;
    switch (interval) {
      case YEAR:
        dateString = "%Y-01-01";
        break;
      case MONTH:
        dateString = "%Y-%m-01";
        break;
      default: // TODO: Everything else we drop to day resolution (could do hour, min, sec
        dateString = "%Y-%m-%d";
    }



    // Convert from milliseconds to data
    // https://stackoverflow.com/questions/29892152/convert-milliseconds-to-date-in-mongodb-aggregation-pipeline-for-group-by
    final Document tsToDate = new Document("ts",
        new Document("$add", Arrays.asList(new Date(0),
            String.format("$%s.%s", BaleenProperties.PROPERTIES, BaleenProperties.DOCUMENT_DATE))));
    aggregation.add(Aggregates.project(tsToDate));
    // then create a grouping
    final Document dateToString = new Document("date",
        new Document("$dateToString",
            new Document()
                .append("format", dateString)
                .append("date", "$ts")));
    aggregation.add(Aggregates.project(dateToString));
    aggregation.add(Aggregates.group("$date", Accumulators.sum("count", 1)));
    aggregation
        .add(Aggregates
            .project(Projections.fields(Projections.computed("term", "$_id"), Projections.include("count"))));

    return aggregate(aggregation, TermBin.class).map(t -> {
      final LocalDate date = LocalDate.parse(t.getTerm());
      return new TimeBin(date.atStartOfDay(ZoneOffset.UTC).toInstant(), t.getCount());
    });
  }

  @Override
  public Flux<TermBin> countByField(final Optional<DocumentFilter> documentFilter, final List<String> path,
      final int size) {
    return termAggregation(DocumentFilters.createFilter(documentFilter), path, size);
  }

}

