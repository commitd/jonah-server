package io.committed.ketos.plugins.data.mongo.factory;

import java.util.Map;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.support.data.mongo.AbstractMongoDataProviderFactory;
import io.committed.ketos.common.providers.baleen.CrudMentionProvider;
import io.committed.ketos.plugins.data.mongo.data.BaleenCodecs;
import io.committed.ketos.plugins.data.mongo.data.BaleenMongoConstants;
import io.committed.ketos.plugins.data.mongo.providers.MongoCrudMentionProvider;
import reactor.core.publisher.Mono;

public class MongoCrudMentionProviderFactory
    extends AbstractMongoDataProviderFactory<CrudMentionProvider> {

  public MongoCrudMentionProviderFactory() {
    super("baleen-mongo-crud-mentions", CrudMentionProvider.class, BaleenMongoConstants.DEFAULT_DATABASE,
        BaleenMongoConstants.DEFAULT_MENTION_COLLECTION);
  }

  @Override
  public Mono<CrudMentionProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final MongoDatabase database = buildMongoDatabase(settings)
        .withCodecRegistry(BaleenCodecs.codecRegistry());
    final String collectionName = getCollectionName(settings);

    final String entityCollection =
        (String) settings.getOrDefault("entityCollection", BaleenMongoConstants.DEFAULT_ENTITY_COLLECTION);
    final String relationCollection =
        (String) settings.getOrDefault("relationCollection", BaleenMongoConstants.DEFAULT_RELATION_COLLECTION);

    return Mono.just(new MongoCrudMentionProvider(dataset, datasource, database, collectionName, entityCollection,
        relationCollection));
  }
}
