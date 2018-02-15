
package io.committed.ketos.plugins.data.mongo.providers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.ketos.common.baleenconsumer.OutputDocument;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.providers.baleen.MetadataProvider;
import reactor.core.publisher.Flux;

public class MongoMetadataProvider extends AbstractBaleenMongoDataProvider<OutputDocument>
    implements MetadataProvider {

  public MongoMetadataProvider(final String dataset, final String datasource,
      final MongoDatabase database, final String collectionName) {
    super(dataset, datasource, database, collectionName, OutputDocument.class);
  }

  @Override
  public Flux<TermBin> countByKey(final Optional<String> key, final int size) {
    final Optional<Bson> filter = filterByKey(key);
    return metadataAggregation(filter, BaleenProperties.METADATA_KEY, size);
  }

  @Override
  public Flux<TermBin> countByValue(final Optional<String> key, final int size) {
    final Optional<Bson> filter = filterByKey(key);
    return metadataAggregation(filter, BaleenProperties.METADATA_VALUE, size);
  }

  private Optional<Bson> filterByKey(final Optional<String> key) {
    return key.map(k -> {
      final String field = String.format("%s.%s", BaleenProperties.METADATA, BaleenProperties.METADATA_KEY);
      return Filters.eq(field, key.get());
    });
  }

  protected Flux<TermBin> metadataAggregation(final Optional<Bson> filter, final String field, final int size) {

    final List<Bson> aggregation = new LinkedList<>();

    aggregation.add(Aggregates.project(Projections.include(BaleenProperties.METADATA)));
    aggregation.add(Aggregates.unwind("$" + BaleenProperties.METADATA));
    filter.ifPresent(f -> aggregation.add(Aggregates.match(f)));
    aggregation.add(Aggregates.replaceRoot("$" + BaleenProperties.METADATA));
    aggregation.add(Aggregates.sortByCount("$" + field));

    aggregation.add(
        Aggregates.project(Projections.fields(Projections.computed("term", "$_id"), Projections.include("count"))));
    return aggregate(aggregation, TermBin.class).take(size);
  }

}

