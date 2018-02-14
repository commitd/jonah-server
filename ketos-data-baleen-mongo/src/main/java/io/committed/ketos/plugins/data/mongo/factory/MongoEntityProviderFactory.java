package io.committed.ketos.plugins.data.mongo.factory;

import java.util.Map;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.support.data.mongo.AbstractMongoDataProviderFactory;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.plugins.data.mongo.data.BaleenCodecs;
import reactor.core.publisher.Mono;

public class MongoEntityProviderFactory extends AbstractMongoDataProviderFactory<EntityProvider> {

  public MongoEntityProviderFactory() {
    super("baleen-mongo-entities", EntityProvider.class, "baleen", "entities");
  }

  @Override
  public Mono<EntityProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final MongoDatabase database = buildMongoDatabase(settings)
        .withCodecRegistry(BaleenCodecs.codecRegistry());
    final String collectionName = getCollectionName(settings);

    // return Mono.just(new MongoEntityProvider(dataset, datasource, database, collectionName));

    return Mono.empty();
  }

}
