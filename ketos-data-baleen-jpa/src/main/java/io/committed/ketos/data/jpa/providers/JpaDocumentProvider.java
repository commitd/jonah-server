package io.committed.ketos.data.jpa.providers;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import io.committed.invest.core.constants.TimeInterval;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.analytic.TimeRange;
import io.committed.invest.support.data.jpa.AbstractJpaDataProvider;
import io.committed.invest.support.data.utils.OffsetLimitPagable;
import io.committed.ketos.common.constants.ItemTypes;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.general.NamedGeoLocation;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentProbe;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import io.committed.ketos.common.graphql.output.DocumentSearch;
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
      final JpaDocumentRepository documents, final JpaDocumentMetadataRepository metadata) {
    super(dataset, datasource);
    this.documents = documents;
    this.metadataRepo = metadata;

  }

  @Override
  public Mono<BaleenDocument> getById(final String id) {
    return Mono.justOrEmpty(documents.findByExternalId(id).map(this::addMetadataAndConvert));
  }

  @Override
  public DocumentSearchResult search(final DocumentSearch documentSearch, final int offset, final int size) {
    final String query = documentSearch.getDocumentFilter().getContent();

    final Mono<Long> count = Mono.just(documents.countByContentContaining(query));

    final Pageable pageable = new OffsetLimitPagable(offset, size);
    final Flux<BaleenDocument> fromStream = Flux.fromStream(
        documents.findByContentContaining(query, pageable).map(this::addMetadataAndConvert));

    return DocumentSearchResult.builder()
        .results(fromStream)
        .total(count)
        .build();
  }

  @Override
  public Flux<BaleenDocument> getAll(final int offset, final int limit) {
    final Page<JpaDocument> page = documents.findAll(PageRequest.of(offset / limit, limit));
    return Flux.fromStream(page.stream().map(this::addMetadataAndConvert));
  }

  @Override
  public Mono<Long> count() {
    return Mono.just(documents.count());
  }

  @Override
  public Flux<TimeBin> countByDate(final Optional<DocumentFilter> documentFilter, final TimeInterval interval) {
    // NOTE: Only group by day

    return Flux.fromStream(documents.countByDate()).map(t -> {
      final LocalDate date = LocalDate.parse(t.getTerm());
      return new TimeBin(date.atStartOfDay(ZoneOffset.UTC).toInstant(), t.getCount());
    });

  }

  @Override
  public Flux<BaleenDocument> getByExample(final DocumentProbe probe, final int offset, final int limit) {
    // TODO: Review matchers for each field
    final ExampleMatcher matcher = ExampleMatcher.matching()
        .withMatcher("content", GenericPropertyMatcher::contains);
    return Flux.fromIterable(documents.findAll(Example.of(new JpaDocument(probe), matcher)))
        // This is inefficent, we'd be better with a pagination on the query... but there isn't any in
        // Spring Data...!
        .skip(offset)
        .take(limit)
        .map(this::addMetadataAndConvert);
  }

  @Override
  public Flux<TermBin> countByField(final Optional<DocumentFilter> documentFilter, final List<String> path,
      final int size) {
    // TODO Ignores the documentFilter
    // TODO Doesn't support metadata, that would require join (not an issue but more complexity
    // considering how little use this has)
    // TODO: Only limited types when it is supported

    if (path.size() < 2 && !path.get(0).equals("info")) {
      return Flux.empty();
    }

    switch (path.get(1)) {
      case "type":
        return Flux.fromStream(documents.countByType(size));
      case "classification":
        return Flux.fromStream(documents.countByClassification(size));
      case "language":
        return Flux.fromStream(documents.countByLanguage(size));

    }

    return Flux.empty();
  }



  private BaleenDocument addMetadataAndConvert(final JpaDocument document) {
    final Flux<JpaDocumentMetadata> metadata =
        Flux.fromStream(metadataRepo.findByDocId(document.getExternalId()));
    return document.toBaleenDocument(metadata);
  }

  @Override
  public Flux<TermBin> countByJoinedField(final Optional<DocumentFilter> documentFilter, final ItemTypes joinedType,
      final List<String> path, final int size) {
    return Flux.empty();
  }

  @Override
  public Flux<TimeBin> countByJoinedDate(final Optional<DocumentFilter> documentFilter, final ItemTypes joinedType,
      final TimeInterval interval) {
    return Flux.empty();
  }


  @Override
  public Flux<NamedGeoLocation> documentLocations(final Optional<DocumentFilter> documentFilter, final int size) {
    return Flux.empty();
  }

  @Override
  public Mono<TimeRange> documentTimeRange(final Optional<DocumentFilter> documentFilter) {
    return Mono.empty();
  }

  @Override
  public Mono<TimeRange> entityTimeRange(final Optional<DocumentFilter> documentFilter) {
    return Mono.empty();
  }

}
