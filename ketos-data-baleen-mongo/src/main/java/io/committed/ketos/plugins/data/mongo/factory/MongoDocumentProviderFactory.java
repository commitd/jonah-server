package io.committed.ketos.plugins.data.mongo.factory;

import java.util.Map;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.support.data.mongo.AbstractMongoDataProviderFactory;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.plugins.data.mongo.providers.MongoDocumentProvider;
import reactor.core.publisher.Mono;

public class MongoDocumentProviderFactory
    extends AbstractMongoDataProviderFactory<DocumentProvider> {

  public MongoDocumentProviderFactory() {
    super("baleen-mongo-documents", DocumentProvider.class, "baleen", "documents");
  }

  @Override
  public Mono<DocumentProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    final MongoDatabase database = buildMongoDatabase(settings);
    final String collectionName = getCollectionName(settings);

    return Mono.just(new MongoDocumentProvider(dataset, datasource, database, collectionName));
  }

}
