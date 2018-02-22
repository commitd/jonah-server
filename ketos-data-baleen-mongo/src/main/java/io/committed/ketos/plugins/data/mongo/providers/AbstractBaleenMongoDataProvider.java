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
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.constants.TimeInterval;
import io.committed.invest.support.data.mongo.AbstractMongoCollectionDataProvider;
import io.committed.invest.support.data.utils.FieldUtils;
import io.committed.ketos.common.constants.BaleenProperties;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractBaleenMongoDataProvider<T> extends AbstractMongoCollectionDataProvider<T> {

  public AbstractBaleenMongoDataProvider(final String dataset, final String datasource,
      final MongoDatabase database, final String collectionName, final Class<T> clazz) {
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

  protected Flux<TermBin> termAggregation(final Optional<Bson> filter, final List<String> path, final int size) {
    final List<Bson> aggregation = new LinkedList<>();
    filter.ifPresent(f -> aggregation.add(Aggregates.match(f)));
    return termAggregation(aggregation, path, size);
  }

  protected Flux<TermBin> termAggregation(final List<Bson> aggregation, final List<String> path, final int size) {
    final String fieldName = FieldUtils.joinField(path);
    final String field = "$" + fieldName;

    aggregation.add(Aggregates.sortByCount(field));
    aggregation.add(
        Aggregates.project(Projections.fields(Projections.computed("term", "$_id"), Projections.include("count"))));
    return aggregate(aggregation, Document.class)
        .map(d -> {
          final Object k = d.get("term");
          final long c = d.getInteger("count", 0);
          return new TermBin(k.toString(), c);
        })
        .take(size);
  }

  protected Flux<TimeBin> timelineAggregation(final List<Bson> aggregation, final TimeInterval interval,
      final Object timestampField, final int toMillisMultiplier) {

    String dateString;
    switch (interval) {
      case YEAR:
        dateString = "%Y-01-01";
        break;
      case MONTH:
        dateString = "%Y-%m-01";
        break;
      default: // TODO: Everything else we drop to day resolution (could do hour, min, sec but we'd need to get the
               // format right)
        dateString = "%Y-%m-%d";
    }

    // Convert from milliseconds to data
    // https://stackoverflow.com/questions/29892152/convert-milliseconds-to-date-in-mongodb-aggregation-pipeline-for-group-by
    final Document tsToDate = new Document("ts",
        new Document("$add",
            Arrays.asList(new Date(0), new Document("$multiply", Arrays.asList(toMillisMultiplier, timestampField)))));
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

    return aggregate(aggregation, TermBin.class)
        // Cna't to anything with empty dates
        .filter(t -> t.getTerm() != null && !t.getTerm().isEmpty())
        .map(t -> {
          final LocalDate date = LocalDate.parse(t.getTerm());
          return new TimeBin(date.atStartOfDay(ZoneOffset.UTC).toInstant(), t.getCount());
        });
  }
}
