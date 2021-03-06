package io.committed.ketos.data.elasticsearch.providers;

import java.util.List;
import java.util.Optional;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.utils.FieldUtils;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.invest.support.data.elasticsearch.SearchHits;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.ElasticsearchMapping;
import io.committed.ketos.common.baleenconsumer.OutputRelation;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.intermediate.RelationSearchResult;
import io.committed.ketos.common.graphql.output.RelationSearch;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.data.elasticsearch.filters.RelationFilters;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;

/** Elasticsearch RelationProvider. */
public class ElasticsearchRelationProvider
    extends AbstractElasticsearchServiceDataProvider<OutputRelation, EsRelationService>
    implements RelationProvider {

  private static final int ALL_RELATIONS = 999;

  public ElasticsearchRelationProvider(
      final String dataset, final String datasource, final EsRelationService service) {
    super(dataset, datasource, service);
  }

  @Override
  public Flux<BaleenRelation> getByDocument(final BaleenDocument document) {
    return getService().getByDocumentId(document.getId()).map(Converters::toBaleenRelation);
  }

  @Override
  public Flux<BaleenRelation> getAll(final int offset, final int limit) {
    return getService().getAll(offset, limit).map(Converters::toBaleenRelation);
  }

  @Override
  public Flux<BaleenRelation> getSourceRelations(final BaleenMention mention) {
    return getService()
        .search(QueryBuilders.termQuery("source.externalId", mention.getId()), 0, ALL_RELATIONS)
        .flatMapMany(SearchHits::getResults)
        .map(Converters::toBaleenRelation);
  }

  @Override
  public Flux<BaleenRelation> getTargetRelations(final BaleenMention mention) {
    return getService()
        .search(QueryBuilders.termQuery("target.externalId", mention.getId()), 0, ALL_RELATIONS)
        .flatMapMany(SearchHits::getResults)
        .map(Converters::toBaleenRelation);
  }

  @Override
  public Mono<BaleenRelation> getById(final String id) {
    return getService().getByExternalId(id).map(Converters::toBaleenRelation);
  }

  @Override
  public Mono<Long> count() {
    return getService().count();
  }

  @Override
  public Flux<TermBin> countByField(
      final Optional<RelationFilter> filter, final List<String> path, final int size) {
    final Optional<QueryBuilder> query = RelationFilters.toQuery(filter, "");
    final String field = FieldUtils.joinField(ElasticsearchMapping.toAggregationPath(path));
    return getService().termAggregation(query, field, size);
  }

  @Override
  public RelationSearchResult search(
      final RelationSearch search, final int offset, final int limit) {
    final Optional<QueryBuilder> query = RelationFilters.toQuery(search);

    if (query.isPresent()) {
      final Mono<SearchHits<OutputRelation>> hits = getService().search(query.get(), offset, limit);
      final Flux<BaleenRelation> results =
          hits.flatMapMany(SearchHits::getResults).map(Converters::toBaleenRelation);
      final Mono<Long> total = hits.map(SearchHits::getTotal);
      return new RelationSearchResult(results, total, offset, limit);
    } else {
      return new RelationSearchResult(getAll(offset, limit), count(), offset, limit);
    }
  }
}
