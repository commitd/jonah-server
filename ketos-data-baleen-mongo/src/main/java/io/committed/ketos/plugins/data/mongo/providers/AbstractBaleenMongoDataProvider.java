package io.committed.ketos.plugins.data.mongo.providers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.core.dto.analytic.TermBin;
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
    final String field = "$" + FieldUtils.joinField(path);

    final List<Bson> aggregation = new LinkedList<>();

    filter.ifPresent(aggregation::add);

    aggregation.add(Aggregates.group(field, Accumulators.sum("count", 1)));
    aggregation.add(
        Aggregates.project(Projections.fields(Projections.computed("term", "$_id"), Projections.include("count"))));
    return aggregate(aggregation, TermBin.class).take(size);
  }
}
