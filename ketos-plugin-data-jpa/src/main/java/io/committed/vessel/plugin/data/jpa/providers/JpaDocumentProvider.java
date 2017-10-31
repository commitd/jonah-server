package io.committed.vessel.plugin.data.jpa.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DatasourceConstants;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.vessel.plugin.data.jpa.dao.JpaDocument;
import io.committed.vessel.plugin.data.jpa.dao.JpaDocumentMetadata;
import io.committed.vessel.plugin.data.jpa.repository.JpaDocumentMetadataRepository;
import io.committed.vessel.plugin.data.jpa.repository.JpaDocumentRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class JpaDocumentProvider implements DocumentProvider {

  private final String corpus;

  private final JpaDocumentRepository documents;
  private final JpaDocumentMetadataRepository metadataRepo;

  @Autowired
  public JpaDocumentProvider(final String corpus,
      final JpaDocumentRepository documents,
      final JpaDocumentMetadataRepository metadata) {
    this.corpus = corpus;
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
    final Page<JpaDocument> page = documents.findAll(PageRequest.of(0, limit));
    return Flux.fromStream(page.stream().map(this::addMetadataAndConvert));
  }

  private BaleenDocument addMetadataAndConvert(final JpaDocument document) {
    final Flux<JpaDocumentMetadata> metadata =
        Flux.fromStream(metadataRepo.findByDocId(document.getExternalId()));
    return document.toBaleenDocument(metadata);
  }

  @Override
  public String getDatasource() {
    return DatasourceConstants.SQL;
  }

  @Override
  public String getCorpus() {
    return corpus;
  }

}
