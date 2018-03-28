package io.committed.ketos.plugins.data.mongo.factory;

import java.util.Map;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.support.data.mongo.AbstractMongoDataProviderFactory;
import io.committed.ketos.common.constants.BaleenMongoConstants;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.plugins.data.mongo.data.BaleenCodecs;
import io.committed.ketos.plugins.data.mongo.providers.MongoDocumentProvider;
import reactor.core.publisher.Mono;

/**
 * A factory for creating DocumentProviders.
 */
public class MongoDocumentProviderFactory
    extends AbstractMongoDataProviderFactory<DocumentProvider> {

  public MongoDocumentProviderFactory() {
    super("baleen-mongo-documents", DocumentProvider.class, BaleenMongoConstants.DEFAULT_DATABASE,
        BaleenMongoConstants.DEFAULT_DOCUMENT_COLLECTION);
  }

  @Override
  public Mono<DocumentProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final MongoDatabase database = buildMongoDatabase(settings)
        .withCodecRegistry(BaleenCodecs.codecRegistry());
    final String collectionName = getCollectionName(settings);

    final String mentionCollection =
        (String) settings.getOrDefault("mentionCollection", BaleenMongoConstants.DEFAULT_MENTION_COLLECTION);
    final String entityCollection =
        (String) settings.getOrDefault("entityCollection", BaleenMongoConstants.DEFAULT_ENTITY_COLLECTION);
    final String relationCollection =
        (String) settings.getOrDefault("relationCollection", BaleenMongoConstants.DEFAULT_RELATION_COLLECTION);

    return Mono.just(new MongoDocumentProvider(dataset, datasource, database, collectionName,
        mentionCollection, entityCollection, relationCollection));
  }

}
