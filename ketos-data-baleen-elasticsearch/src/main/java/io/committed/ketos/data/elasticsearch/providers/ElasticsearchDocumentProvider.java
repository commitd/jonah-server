package io.committed.ketos.data.elasticsearch.providers;

import java.util.List;
import java.util.Optional;
import org.elasticsearch.index.query.QueryBuilder;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.constants.TimeInterval;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.invest.support.data.utils.FieldUtils;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.OutputDocument;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.constants.ItemTypes;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import io.committed.ketos.common.graphql.output.DocumentSearch;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.data.elasticsearch.filters.DocumentFilters;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchDocumentProvider
    extends AbstractElasticsearchServiceDataProvider<OutputDocument, EsDocumentService>
    implements DocumentProvider {

  private String mentionType;
  private String entityType;
  private String relationType;

  public ElasticsearchDocumentProvider(final String dataset, final String datasource,
      final EsDocumentService documentService, final String mentionType, final String entityType,
      final String relationType) {
    super(dataset, datasource, documentService);
    this.mentionType = mentionType;
    this.entityType = entityType;
    this.relationType = relationType;
  }

  @Override
  public Mono<BaleenDocument> getById(final String id) {
    return getService().getByExternalId(id)
        .map(Converters::toBaleenDocument);
  }

  @Override
  public Mono<Long> count() {
    return getService().count();

  }

  @Override
  public Flux<BaleenDocument> getAll(final int offset, final int size) {
    return getService().getAll(offset, size).map(Converters::toBaleenDocument);
  }

  @Override
  public DocumentSearchResult search(final DocumentSearch documentSearch, final int offset, final int limit) {

    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentSearch, mentionType, entityType, relationType);

    // TODO: count is available from ES, but we can't get it via search() which just returns a flux

    Flux<BaleenDocument> results;
    if (query.isPresent()) {
      results = getService().search(query.get(), offset, limit).map(Converters::toBaleenDocument);
    } else {
      results = getService().getAll(offset, limit).map(Converters::toBaleenDocument);
    }

    return new DocumentSearchResult(results, Mono.empty());
  }

  @Override
  public Flux<TermBin> countByField(final Optional<DocumentFilter> documentFilter, final List<String> path,
      final int size) {
    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);
    final String field = FieldUtils.joinField(path);
    return getService().nestedTermAggregation(query, BaleenProperties.PROPERTIES, field, size);
  }

  @Override
  public Flux<TimeBin> countByDate(final Optional<DocumentFilter> documentFilter, final TimeInterval interval) {
    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);
    final String field = BaleenProperties.PROPERTIES + "." + BaleenProperties.DOCUMENT_DATE;

    return getService().nestedTimelineAggregation(query, interval, BaleenProperties.PROPERTIES, field);
  }


  @Override
  public Flux<TimeBin> countByJoinedDate(final Optional<DocumentFilter> documentFilter, final ItemTypes joinedType,
      final TimeInterval interval) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Flux<TermBin> countByJoinedField(final Optional<DocumentFilter> documentFilter, final ItemTypes joinedType,
      final List<String> path, final int size) {
    // TODO Auto-generated method stub
    return null;
  }


}

