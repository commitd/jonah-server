package io.committed.ketos.plugins.data.mongo.factory;

import java.util.Map;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.support.data.mongo.AbstractMongoDataProviderFactory;
import io.committed.ketos.common.providers.baleen.CrudDocumentProvider;
import io.committed.ketos.plugins.data.mongo.data.BaleenCodecs;
import io.committed.ketos.plugins.data.mongo.data.BaleenMongoConstants;
import io.committed.ketos.plugins.data.mongo.providers.MongoCrudDocumentProvider;
import reactor.core.publisher.Mono;

public class MongoCrudDocumentProviderFactory
    extends AbstractMongoDataProviderFactory<CrudDocumentProvider> {

  public MongoCrudDocumentProviderFactory() {
    super("baleen-mongo-crud-documents", CrudDocumentProvider.class, BaleenMongoConstants.DEFAULT_DATABASE,
        BaleenMongoConstants.DEFAULT_DOCUMENT_COLLECTION);
  }

  @Override
  public Mono<CrudDocumentProvider> build(final String dataset, final String datasource,
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

    return Mono.just(new MongoCrudDocumentProvider(dataset, datasource, database, collectionName,
        mentionCollection, entityCollection, relationCollection));
  }
}
