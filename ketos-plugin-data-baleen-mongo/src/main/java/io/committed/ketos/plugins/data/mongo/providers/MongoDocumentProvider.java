package io.committed.ketos.plugins.data.mongo.providers;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.plugins.data.mongo.dao.MongoDocument;
import io.committed.ketos.plugins.data.mongo.repository.BaleenDocumentRepository;
import io.committed.vessel.server.data.providers.AbstractDataProvider;
import io.committed.vessel.server.data.providers.DatabaseConstants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoDocumentProvider extends AbstractDataProvider implements DocumentProvider {

  private final BaleenDocumentRepository documents;

  public MongoDocumentProvider(final String dataset, final String datasource,
      final BaleenDocumentRepository documents) {
    super(dataset, datasource);
    this.documents = documents;
  }

  @Override
  public Mono<BaleenDocument> getById(final String id) {
    return documents.findByExternalId(id).map(MongoDocument::toDocument);
  }

  @Override
  public Flux<BaleenDocument> search(final String search, final int limit) {
    return documents
        .searchDocuments(search)
        .take(limit)
        .map(MongoDocument::toDocument);
  }

  @Override
  public Flux<BaleenDocument> all(final int limit) {
    return documents.findAll().take(limit).map(MongoDocument::toDocument);
  }

  @Override
  public String getDatabase() {
    return DatabaseConstants.MONGO;
  }


}
