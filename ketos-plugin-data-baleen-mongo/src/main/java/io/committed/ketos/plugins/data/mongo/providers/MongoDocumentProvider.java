package io.committed.ketos.plugins.data.mongo.providers;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.plugins.data.mongo.dao.MongoDocument;
import io.committed.ketos.plugins.data.mongo.repository.BaleenDocumentRepository;
import io.committed.vessel.server.data.providers.DatasourceConstants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoDocumentProvider implements DocumentProvider {

  private final BaleenDocumentRepository documents;
  private final String corpus;

  public MongoDocumentProvider(final String corpus, final BaleenDocumentRepository documents) {
    this.corpus = corpus;
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
  public String getDatasource() {
    return DatasourceConstants.MONGO;
  }

  @Override
  public String getDataset() {
    return corpus;
  }

}
