package io.committed.ketos.plugins.data.mongo.factory;

import java.util.Map;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.support.data.mongo.AbstractMongoDataProviderFactory;
import io.committed.ketos.common.constants.BaleenMongoConstants;
import io.committed.ketos.common.providers.baleen.CrudEntityProvider;
import io.committed.ketos.plugins.data.mongo.data.BaleenCodecs;
import io.committed.ketos.plugins.data.mongo.providers.MongoCrudEntityProvider;
import reactor.core.publisher.Mono;

public class MongoCrudEntityProviderFactory
    extends AbstractMongoDataProviderFactory<CrudEntityProvider> {

  public MongoCrudEntityProviderFactory() {
    super("baleen-mongo-crud-entities", CrudEntityProvider.class, BaleenMongoConstants.DEFAULT_DATABASE,
        BaleenMongoConstants.DEFAULT_ENTITY_COLLECTION);
  }

  @Override
  public Mono<CrudEntityProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final MongoDatabase database = buildMongoDatabase(settings)
        .withCodecRegistry(BaleenCodecs.codecRegistry());
    final String collectionName = getCollectionName(settings);

    return Mono.just(new MongoCrudEntityProvider(dataset, datasource, database, collectionName));
  }
}
