package io.committed.ketos.data.elasticsearch.providers;

import java.util.List;
import java.util.Optional;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.constants.TimeInterval;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.invest.support.data.utils.FieldUtils;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.OutputDocument;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import io.committed.ketos.common.graphql.output.DocumentSearch;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.data.elasticsearch.filters.DocumentFilters;
import io.committed.ketos.data.elasticsearch.filters.MentionFilters;
import io.committed.ketos.data.elasticsearch.filters.RelationFilters;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchDocumentProvider
    extends AbstractElasticsearchServiceDataProvider<OutputDocument, EsDocumentService>
    implements DocumentProvider {

  public ElasticsearchDocumentProvider(final String dataset, final String datasource,
      final EsDocumentService documentService) {
    super(dataset, datasource, documentService);
  }

  @Override
  public Mono<BaleenDocument> getById(final String id) {
    return getService().getById(id)
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
    final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

    if (documentSearch.getDocumentFilter() != null) {
      DocumentFilters.toQuery(documentSearch.getDocumentFilter())
          .ifPresent(queryBuilder::must);
    }

    if (documentSearch.getMentionFilters() != null) {
      documentSearch.getMentionFilters().stream()
          // Note this use toEntities, which ignores the docId
          .map(f -> MentionFilters.toMentionsQuery(f, EsDocument.MENTIONS_PREFIX))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .forEach(queryBuilder::must);
    }

    if (documentSearch.getRelationFilters() != null) {
      documentSearch.getRelationFilters().stream()
          .map(f -> RelationFilters.toQuery(f, EsDocument.RELATIONS_PREFIX))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .forEach(queryBuilder::must);
    }

    final Flux<BaleenDocument> results =
        getService().search(queryBuilder, offset, limit).map(Converters::toBaleenDocument);

    // TODO: count is available from ES, but we can't get it via search() which just returns a flux

    return new DocumentSearchResult(results, Mono.empty());
  }

  @Override
  public Flux<TermBin> countByField(final Optional<DocumentFilter> documentFilter, final List<String> path,
      final int size) {

    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);

    // The document info is flattened here

    String field;
    if (path.get(0).equals("info")) {
      field = path.get(path.size() - 1);
      if (field.equals("source")) {
        field = "sourceUri";
      }
    } else {
      // If other (metadata) then just let it be
      field = FieldUtils.joinField(path);
    }

    return getService().termAggregation(query, field, size);
  }

  @Override
  public Flux<TimeBin> countByDate(final Optional<DocumentFilter> documentFilter, final TimeInterval interval) {

    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);

    return getService().timelineAggregation(query, "dateAccessed", interval);
  }

}

