package io.committed.ketos.plugins.data.mongo.factory;

import java.util.Map;

import reactor.core.publisher.Mono;

import com.mongodb.reactivestreams.client.MongoDatabase;

import io.committed.invest.support.data.mongo.AbstractMongoDataProviderFactory;
import io.committed.ketos.common.constants.BaleenMongoConstants;
import io.committed.ketos.common.providers.baleen.CrudRelationProvider;
import io.committed.ketos.plugins.data.mongo.data.BaleenCodecs;
import io.committed.ketos.plugins.data.mongo.providers.MongoCrudRelationProvider;

/** A factory for creating Mongo CrudRelationProviders. */
public class MongoCrudRelationProviderFactory
    extends AbstractMongoDataProviderFactory<CrudRelationProvider> {

  public MongoCrudRelationProviderFactory() {
    super(
        "baleen-mongo-crud-relations",
        CrudRelationProvider.class,
        BaleenMongoConstants.DEFAULT_DATABASE,
        BaleenMongoConstants.DEFAULT_RELATION_COLLECTION);
  }

  @Override
  public Mono<CrudRelationProvider> build(
      final String dataset, final String datasource, final Map<String, Object> settings) {
    final MongoDatabase database =
        buildMongoDatabase(settings).withCodecRegistry(BaleenCodecs.codecRegistry());
    final String collectionName = getCollectionName(settings);

    return Mono.just(new MongoCrudRelationProvider(dataset, datasource, database, collectionName));
  }
}
