package io.committed.ketos.plugins.data.mongo.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.committed.ketos.plugins.data.baleen.BaleenDocument;
import io.committed.ketos.plugins.data.mongo.dao.MongoDocument;
import io.committed.ketos.plugins.data.mongo.repository.BaleenDocumentRepository;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DocumentProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MongoDocumentProvider implements DocumentProvider {

  private final BaleenDocumentRepository documents;

  @Autowired
  public MongoDocumentProvider(final BaleenDocumentRepository documents) {
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

}
