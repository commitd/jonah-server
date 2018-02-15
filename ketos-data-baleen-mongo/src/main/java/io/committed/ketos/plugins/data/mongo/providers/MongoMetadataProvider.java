
package io.committed.ketos.plugins.data.mongo.providers;

import java.util.Arrays;
import java.util.Optional;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
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
    // db.documents.aggregate([
    // {$match: {"$metadata.key": key}},
    // {$group: {_id:"$metadata.key" , count:{ $sum: 1}}},
    // {$project: {term: "$_id", count: "$count"}}
    // ])

    final Optional<Bson> filter = filterByKey(key);

    return termAggregation(filter, Arrays.asList(BaleenProperties.METADATA, BaleenProperties.METADATA_KEY), size);
  }

  @Override
  public Flux<TermBin> countByValue(final Optional<String> key, final int size) {
    final Optional<Bson> filter = filterByKey(key);

    return termAggregation(filter, Arrays.asList(BaleenProperties.METADATA, BaleenProperties.METADATA_VALUE), size);
  }

  private Optional<Bson> filterByKey(final Optional<String> key) {
    return key.map(k -> {
      final String field = String.format("$%s.%s", BaleenProperties.METADATA, BaleenProperties.METADATA_KEY);
      return Filters.eq(field, key.get());
    });
  }

}

