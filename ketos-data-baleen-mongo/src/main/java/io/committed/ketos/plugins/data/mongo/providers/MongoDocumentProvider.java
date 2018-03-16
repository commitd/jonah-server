
package io.committed.ketos.plugins.data.mongo.providers;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.analytic.TimeRange;
import io.committed.invest.core.constants.TimeInterval;
import io.committed.invest.core.utils.FieldUtils;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.OutputDocument;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.constants.BaleenTypes;
import io.committed.ketos.common.constants.ItemTypes;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.general.NamedGeoLocation;
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
        DocumentFilters.createAggregation(documentSearch, getCollectionName(), mentionCollection, entityCollection,
            relationCollection);

    final Flux<BaleenDocument> results = aggregate(aggregation, OutputDocument.class)
        .skip(offset)
        .take(size)
        .map(Converters::toBaleenDocument);

    return new DocumentSearchResult(results, Mono.empty(), offset, size);
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

    return new DocumentSearchResult(flux, total, offset, size);
  }



  @Override
  public Flux<TimeBin> countByDate(final Optional<DocumentFilter> documentFilter, final TimeInterval interval) {

    final List<Bson> aggregation = new LinkedList<>();

    DocumentFilters.createFilter(documentFilter).ifPresent(f -> {
      aggregation.add(Aggregates.match(f));
    });

    return timelineAggregation(aggregation, interval,
        FieldUtils.joinField(BaleenProperties.PROPERTIES, BaleenProperties.DOCUMENT_DATE));
  }



  @Override
  public Flux<TermBin> countByField(final Optional<DocumentFilter> documentFilter, final List<String> path,
      final int size) {
    return termAggregation(DocumentFilters.createFilter(documentFilter), path, size);
  }

  @Override
  public Flux<TimeBin> countByJoinedDate(final Optional<DocumentFilter> documentFilter, final ItemTypes joinedType,
      final TimeInterval interval) {

    final List<Bson> aggregation = joinCollection(documentFilter, joinedType);

    aggregation.add(Aggregates.match(
        Filters.and(
            Filters.eq(BaleenProperties.TYPE, BaleenTypes.TEMPORAL),
            Filters.eq(BaleenProperties.PROPERTIES + "." + BaleenProperties.TEMPORAL_PRECISION,
                BaleenProperties.TEMPORAL_PRECISION__EXACT))));

    // It's hard to pick a sensible data here... this will produce a lot of stuff on the start of month
    // or day as
    // it mentions
    // July 2016 for example. We do have a timestampStart and timestampEnd so we could do something but
    // anything is equally odd
    // Baleen outputs seconds here, but mills for value!
    return timelineAggregation(aggregation, interval,
        FieldUtils.joinField(BaleenProperties.PROPERTIES, BaleenProperties.START_TIMESTAMP));
  }


  @Override
  public Flux<TermBin> countByJoinedField(final Optional<DocumentFilter> documentFilter, final ItemTypes joinedType,
      final List<String> path,
      final int size) {

    final List<Bson> aggregation = joinCollection(documentFilter, joinedType);

    return termAggregation(aggregation, path, size);
  }


  // TODO: Quite specific... but perhaps this is better? (no entity/mention field... leave it to the
  // impl)
  @Override
  public Flux<NamedGeoLocation> documentLocations(final Optional<DocumentFilter> documentFilter, final int size) {
    final List<Bson> aggregation = joinCollection(documentFilter, ItemTypes.ENTITY);

    // Get locations, with pois
    final String poiFieldName = BaleenProperties.PROPERTIES + "." + BaleenProperties.POI;
    aggregation.add(Aggregates.match(
        Filters.and(
            Filters.eq(BaleenProperties.TYPE, BaleenTypes.LOCATION),
            Filters.ne(poiFieldName, null))));

    aggregation.add(Aggregates.project(Projections.fields(Projections.computed("poi", "$" + poiFieldName),
        Projections.include(BaleenProperties.VALUE, BaleenProperties.TYPE))));

    return aggregate(aggregation, Document.class)
        .flatMap(d -> {
          final Object pois = d.get("poi");
          final String value = d.get(BaleenProperties.VALUE, "");
          final String type = d.get(BaleenProperties.TYPE, "");

          if (pois != null && pois instanceof Collection && ((Collection) pois).size() >= 2) {

            final Collection<Object> c = (Collection<Object>) pois;
            final Iterator<Object> it = c.iterator();
            final Object lon = it.next();
            final Object lat = it.next();


            if (lon instanceof Double && lat instanceof Double && lat != null && lon != null) {
              return Flux.just(new NamedGeoLocation(value, type, (double) lat, (double) lon));
            }
          }
          return Flux.empty();
        })
        // Distinct so we hopefully drop the excessive junk
        .distinct()
        .take(size);


  }

  @Override
  public Mono<TimeRange> documentTimeRange(final Optional<DocumentFilter> documentFilter) {
    final List<Bson> aggregation = new LinkedList<>();
    DocumentFilters.createFilter(documentFilter).ifPresent(f -> {
      aggregation.add(Aggregates.match(f));
    });
    return calculateTimeRange(aggregation, "properties.documentDate", "properties.documentDate");
  }

  @Override
  public Mono<TimeRange> entityTimeRange(final Optional<DocumentFilter> documentFilter) {
    final List<Bson> aggregation = joinCollection(documentFilter, ItemTypes.ENTITY);

    aggregation.add(Aggregates.match(Filters.and(
        Filters.eq(BaleenProperties.TYPE, BaleenTypes.TEMPORAL),
        Filters.eq(BaleenProperties.PROPERTIES + "." + BaleenProperties.TEMPORAL_PRECISION,
            BaleenProperties.TEMPORAL_PRECISION__EXACT))));

    return calculateTimeRange(aggregation, "properties.timestampStart", "properties.timestampStop")
        .doOnNext(r -> {
          r.setEnd(new Date(r.getEnd().getTime()));
          r.setStart(new Date(r.getStart().getTime()));
        });
  }


  private List<Bson> joinCollection(final Optional<DocumentFilter> documentFilter, final ItemTypes joinedType) {
    final List<Bson> aggregation = new LinkedList<>();

    DocumentFilters.createFilter(documentFilter).ifPresent(f -> {
      aggregation.add(Aggregates.match(f));
    });

    final String collection = itemTypeToJoinCollection(joinedType);


    // Join on the collection and then unwind
    final String JOINED_FIELD = "joined";
    aggregation.add(Aggregates.lookup(collection, "externalId", "docId", JOINED_FIELD));
    aggregation.add(Aggregates.unwind("$" + JOINED_FIELD));
    aggregation.add(Aggregates.replaceRoot("$" + JOINED_FIELD));

    return aggregation;
  }


  protected String itemTypeToJoinCollection(final ItemTypes type) {
    switch (type) {
      case ENTITY:
        return entityCollection;
      case RELATION:
        return relationCollection;
      case MENTION:
        return mentionCollection;
      default:
        throw new IllegalArgumentException("Not supported join collection");
    }
  }
}

