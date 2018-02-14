package io.committed.ketos.plugins.data.mongo.factory;

import java.util.Map;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.support.data.mongo.AbstractMongoDataProviderFactory;
import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.plugins.data.mongo.data.BaleenCodecs;
import reactor.core.publisher.Mono;

public class MongoMetadataProviderFactory
    extends AbstractMongoDataProviderFactory<MetadataProvider> {

  public MongoMetadataProviderFactory() {
    super("baleen-mongo-metadata", MetadataProvider.class, "baleen", "documents");
  }


  @Override
  public Mono<MetadataProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final MongoDatabase database = buildMongoDatabase(settings)
        .withCodecRegistry(BaleenCodecs.codecRegistry());
    final String collectionName = getCollectionName(settings);

    // return Mono.just(new MongoMetadataProvider(dataset, datasource, database, collectionName));

    return Mono.empty();


  }

}
