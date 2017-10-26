package io.committed.vessel.plugin.data.jdbc.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import io.committed.ketos.plugins.data.baleen.BaleenDocument;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DocumentProvider;
import io.committed.vessel.plugin.data.jdbc.dao.SqlDocument;
import io.committed.vessel.plugin.data.jdbc.dao.SqlDocumentMetadata;
import io.committed.vessel.plugin.data.jdbc.repository.SqlDocumentMetadataRepository;
import io.committed.vessel.plugin.data.jdbc.repository.SqlDocumentRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class JpaDocumentProvider implements DocumentProvider {

  private final SqlDocumentRepository documents;
  private final SqlDocumentMetadataRepository metadataRepo;

  @Autowired
  public JpaDocumentProvider(final SqlDocumentRepository documents,
      final SqlDocumentMetadataRepository metadata) {
    this.documents = documents;
    this.metadataRepo = metadata;

  }

  @Override
  public Mono<BaleenDocument> getById(final String id) {
    return Mono.justOrEmpty(documents.findByExternalId(id).map(this::addMetadataAndConvert));
  }

  @Override
  public Flux<BaleenDocument> search(final String search, final int limit) {
    // TODO Could implemnent this with SQL like (slow) or something DB specific
    return Flux.empty();
  }

  @Override
  public Flux<BaleenDocument> all(final int limit) {
    final Page<SqlDocument> page = documents.findAll(PageRequest.of(0, limit));
    return Flux.fromStream(page.stream().map(this::addMetadataAndConvert));
  }

  private BaleenDocument addMetadataAndConvert(final SqlDocument document) {
    final Flux<SqlDocumentMetadata> metadata =
        Flux.fromStream(metadataRepo.findByDocId(document.getExternalId()));
    return document.toBaleenDocument(metadata);
  }

}
