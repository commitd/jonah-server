package io.committed.vessel.plugin.data.jpa.providers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.vessel.core.dto.analytic.TermBin;
import io.committed.vessel.core.dto.analytic.TimeBin;
import io.committed.vessel.plugin.data.jpa.dao.JpaDocument;
import io.committed.vessel.plugin.data.jpa.dao.JpaDocumentMetadata;
import io.committed.vessel.plugin.data.jpa.repository.JpaDocumentMetadataRepository;
import io.committed.vessel.plugin.data.jpa.repository.JpaDocumentRepository;
import io.committed.vessel.server.data.providers.AbstractDataProvider;
import io.committed.vessel.server.data.providers.DatabaseConstants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class JpaDocumentProvider extends AbstractDataProvider implements DocumentProvider {

  private final JpaDocumentRepository documents;
  private final JpaDocumentMetadataRepository metadataRepo;

  public JpaDocumentProvider(final String dataset, final String datasource,
      final JpaDocumentRepository documents,
      final JpaDocumentMetadataRepository metadata) {
    super(dataset, datasource);
    this.documents = documents;
    this.metadataRepo = metadata;

  }

  @Override
  public Mono<BaleenDocument> getById(final String id) {
    return Mono.justOrEmpty(documents.findByExternalId(id).map(this::addMetadataAndConvert));
  }

  @Override
  public Flux<BaleenDocument> search(final String search, final int offset, final int size) {
    // TODO Could implemnent this with SQL like (slow) or something DB specific
    return Flux.empty();
  }

  @Override
  public Flux<BaleenDocument> all(final int offset, final int limit) {
    final Page<JpaDocument> page = documents.findAll(PageRequest.of(offset / limit, limit));
    return Flux.fromStream(page.stream().map(this::addMetadataAndConvert));
  }

  private BaleenDocument addMetadataAndConvert(final JpaDocument document) {
    final Flux<JpaDocumentMetadata> metadata =
        Flux.fromStream(metadataRepo.findByDocId(document.getExternalId()));
    return document.toBaleenDocument(metadata);
  }

  @Override
  public String getDatabase() {
    return DatabaseConstants.SQL;
  }


  @Override
  public Mono<Long> count() {
    return Mono.just(documents.count());
  }

  // TODO: Implement these

  @Override
  public Flux<TermBin> countByType() {
    // TODO Auto-generated method stub
    return Flux.empty();
  }

  @Override
  public Flux<TimeBin> countByDate() {
    // TODO Auto-generated method stub
    return Flux.empty();
  }

  @Override
  public Mono<Long> countSearchMatches(final String query) {
    // TODO Auto-generated method stub
    return Mono.just(0L);
  }

  @Override
  public Flux<TermBin> countByClassification() {
    // TODO Auto-generated method stub
    return Flux.empty();
  }

  @Override
  public Flux<TermBin> countByLanguage() {
    // TODO Auto-generated method stub
    return Flux.empty();
  }



}
