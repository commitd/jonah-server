package io.committed.ketos.plugins.data.mongo.factory;

import java.util.Map;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.support.data.mongo.AbstractMongoDataProviderFactory;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.plugins.data.mongo.data.BaleenCodecs;
import io.committed.ketos.plugins.data.mongo.data.BaleenMongoConstants;
import io.committed.ketos.plugins.data.mongo.providers.MongoMentionProvider;
import reactor.core.publisher.Mono;

public class MongoMentionProviderFactory extends AbstractMongoDataProviderFactory<MentionProvider> {

  public MongoMentionProviderFactory() {
    super("baleen-mongo-mentions", MentionProvider.class, BaleenMongoConstants.DEFAULT_DATABASE,
        BaleenMongoConstants.DEFAULT_MENTION_COLLECTION);
  }

  @Override
  public Mono<MentionProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final MongoDatabase database = buildMongoDatabase(settings)
        .withCodecRegistry(BaleenCodecs.codecRegistry());
    final String collectionName = getCollectionName(settings);

    return Mono.just(new MongoMentionProvider(dataset, datasource, database, collectionName));
  }

}
