package io.committed.ketos.data.jpa.providers;

import java.time.LocalDate;
import java.time.ZoneOffset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.support.data.jpa.AbstractJpaDataProvider;
import io.committed.invest.support.data.utils.OffsetLimitPagable;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.data.jpa.dao.JpaDocument;
import io.committed.ketos.data.jpa.dao.JpaDocumentMetadata;
import io.committed.ketos.data.jpa.repository.JpaDocumentMetadataRepository;
import io.committed.ketos.data.jpa.repository.JpaDocumentRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class JpaDocumentProvider extends AbstractJpaDataProvider implements DocumentProvider {

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
    final Pageable pageable = new OffsetLimitPagable(offset, size);
    return Flux.fromStream(
        documents.findByContentContaining(search, pageable).map(this::addMetadataAndConvert));
  }

  @Override
  public Mono<Long> countSearchMatches(final String query) {
    return Mono.just(documents.countByContentContaining(query));
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
  public Mono<Long> count() {
    return Mono.just(documents.count());
  }

  @Override
  public Flux<TermBin> countByType() {
    return Flux.fromStream(documents.countByType());
  }

  @Override
  public Flux<TimeBin> countByDate() {
    return Flux.fromStream(documents.countByDate())
        .map(t -> {
          final LocalDate date = LocalDate.parse(t.getTerm());
          return new TimeBin(date.atStartOfDay(ZoneOffset.UTC).toInstant(), t.getCount());
        });

  }



  @Override
  public Flux<TermBin> countByClassification() {
    return Flux.fromStream(documents.countByClassification());
  }

  @Override
  public Flux<TermBin> countByLanguage() {
    return Flux.fromStream(documents.countByLanguage());
  }



}
