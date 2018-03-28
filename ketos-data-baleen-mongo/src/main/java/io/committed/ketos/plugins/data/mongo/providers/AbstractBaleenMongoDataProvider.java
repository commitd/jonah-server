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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.reactivestreams.client.MongoDatabase;

import io.committed.invest.core.constants.TimeInterval;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.analytic.TimeRange;
import io.committed.invest.core.utils.FieldUtils;
import io.committed.invest.support.data.mongo.AbstractMongoCollectionDataProvider;
import io.committed.ketos.common.constants.BaleenProperties;

/**
 * Base class for Mongo CrudDataProviders.
 *
 * @param <R> the reference
 * @param <T> the type to save
 */
public abstract class AbstractBaleenMongoDataProvider<T>
    extends AbstractMongoCollectionDataProvider<T> {

  private static final String COUNT_FIELD = "count";

  public AbstractBaleenMongoDataProvider(
      final String dataset,
      final String datasource,
      final MongoDatabase database,
      final String collectionName,
      final Class<T> clazz) {
    super(dataset, datasource, database, collectionName, clazz);
  }

  protected Mono<T> findByExternalId(final String id) {
    return findByField(BaleenProperties.EXTERNAL_ID, id);
  }

  protected Flux<T> findByDocumentId(final String id) {
    return findAllByField(BaleenProperties.DOC_ID, id);
  }

  @Override
  public Mono<Long> count() {
    return super.count();
  }

  protected Flux<TermBin> termAggregation(
      final Optional<Bson> filter, final List<String> path, final int size) {
    final List<Bson> aggregation = new LinkedList<>();
    filter.ifPresent(f -> aggregation.add(Aggregates.match(f)));
    return termAggregation(aggregation, path, size);
  }

  protected Flux<TermBin> termAggregation(
      final List<Bson> aggregation, final List<String> path, final int size) {
    final String fieldName = FieldUtils.joinField(path);
    final String field = "$" + fieldName;

    aggregation.add(Aggregates.sortByCount(field));
    aggregation.add(
        Aggregates.project(
            Projections.fields(
                Projections.computed("term", "$_id"), Projections.include(COUNT_FIELD))));
    return aggregate(aggregation, Document.class)
        .map(
            d -> {
              final Object k = d.get("term", "NA");
              final long c = d.getInteger(COUNT_FIELD, 0);
              return new TermBin(k.toString(), c);
            })
        .take(size);
  }

  protected Flux<TimeBin> timelineAggregation(
      final List<Bson> aggregation, final TimeInterval interval, final String timestampField) {

    String dateString;
    switch (interval) {
      case YEAR:
        dateString = "%Y-01-01";
        break;
      case MONTH:
        dateString = "%Y-%m-01";
        break;
      default:
        // Everything else we drop to final day resolution (could do hour, min, sec if we wanted)
        dateString = "%Y-%m-%d";
    }

    // Discard anything without a date
    aggregation.add(Aggregates.match(Filters.exists(timestampField)));

    // Convert from milliseconds to data
    // https://stackoverflow.com/questions/29892152/convert-milliseconds-to-date-in-mongodb-aggregation-pipeline-for-group-by
    final Document tsToDate =
        new Document("ts", new Document("$add", Arrays.asList(new Date(0), "$" + timestampField)));
    aggregation.add(Aggregates.project(tsToDate));
    // then create a grouping
    final Document dateToString =
        new Document(
            "date",
            new Document(
                "$dateToString",
                new Document().append("format", dateString).append("date", "$ts")));
    aggregation.add(Aggregates.project(dateToString));
    aggregation.add(Aggregates.group("$date", Accumulators.sum(COUNT_FIELD, 1)));
    aggregation.add(
        Aggregates.project(
            Projections.fields(
                Projections.computed("term", "$_id"), Projections.include(COUNT_FIELD))));

    return aggregate(aggregation, TermBin.class)
        // Cna't to anything with empty dates
        .filter(t -> t.getTerm() != null && !t.getTerm().isEmpty())
        .map(
            t -> {
              final LocalDate date = LocalDate.parse(t.getTerm());
              return new TimeBin(date.atStartOfDay(ZoneOffset.UTC).toInstant(), t.getCount());
            });
  }

  protected Mono<TimeRange> calculateTimeRange(
      final List<Bson> aggregation, final String minField, final String maxField) {

    // This shouldn't be necessary but some of the output from Baleen is bad
    aggregation.add(
        Aggregates.match(Filters.and(Filters.gte(minField, 0), Filters.gte(maxField, 0))));
    aggregation.add(
        Aggregates.group(
            null,
            Accumulators.min("min", "$" + minField),
            Accumulators.max("max", "$" + minField)));

    return aggregate(aggregation, Document.class)
        .map(
            d -> {
              final long min = d.get("min", Number.class).longValue();
              final long max = d.get("max", Number.class).longValue();
              return new TimeRange(new Date(min), new Date(max));
            })
        .next();
  }
}
