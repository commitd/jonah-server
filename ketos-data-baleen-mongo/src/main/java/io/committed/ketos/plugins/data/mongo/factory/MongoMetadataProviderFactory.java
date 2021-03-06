package io.committed.ketos.plugins.data.mongo.factory;

import java.util.Map;

import reactor.core.publisher.Mono;

import com.mongodb.reactivestreams.client.MongoDatabase;

import io.committed.invest.support.data.mongo.AbstractMongoDataProviderFactory;
import io.committed.ketos.common.constants.BaleenMongoConstants;
import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.plugins.data.mongo.data.BaleenCodecs;
import io.committed.ketos.plugins.data.mongo.providers.MongoMetadataProvider;

/** A factory for creating Mongo MetadataProviders. */
public class MongoMetadataProviderFactory
    extends AbstractMongoDataProviderFactory<MetadataProvider> {

  public MongoMetadataProviderFactory() {
    super(
        "baleen-mongo-metadata",
        MetadataProvider.class,
        BaleenMongoConstants.DEFAULT_DATABASE,
        BaleenMongoConstants.DEFAULT_DOCUMENT_COLLECTION);
  }

  @Override
  public Mono<MetadataProvider> build(
      final String dataset, final String datasource, final Map<String, Object> settings) {
    final MongoDatabase database =
        buildMongoDatabase(settings).withCodecRegistry(BaleenCodecs.codecRegistry());
    final String collectionName = getCollectionName(settings);

    return Mono.just(new MongoMetadataProvider(dataset, datasource, database, collectionName));
  }
}
