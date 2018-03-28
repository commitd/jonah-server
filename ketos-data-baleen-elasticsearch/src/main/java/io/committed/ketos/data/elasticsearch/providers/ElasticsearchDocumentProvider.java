package io.committed.ketos.data.elasticsearch.providers;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.aggregations.Children;
import org.elasticsearch.join.aggregations.ChildrenAggregationBuilder;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.joda.time.DateTime;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.invest.core.constants.TimeInterval;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.analytic.TimeRange;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.invest.support.data.elasticsearch.SearchHits;
import io.committed.invest.support.elasticsearch.utils.TimeIntervalUtils;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.ElasticsearchMapping;
import io.committed.ketos.common.baleenconsumer.OutputDocument;
import io.committed.ketos.common.baleenconsumer.OutputEntity;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.constants.BaleenTypes;
import io.committed.ketos.common.constants.ItemTypes;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.general.NamedGeoLocation;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import io.committed.ketos.common.graphql.output.DocumentSearch;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.data.elasticsearch.filters.DocumentFilters;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;

/** Elasticsearch DocumentProvider. */
public class ElasticsearchDocumentProvider
    extends AbstractElasticsearchServiceDataProvider<OutputDocument, EsDocumentService>
    implements DocumentProvider {

  private static final String MAX_AGG = "max";
  private static final String MIN_AGG = "min";
  private static final String TERMS_AGG = "terms";
  private static final String DH_AGG = "dh";
  private static final String CHILDREN_AGG = "children";
  private final String mentionType;
  private final String entityType;
  private final String relationType;
  private final EsEntityService entityService;

  public ElasticsearchDocumentProvider(
      final String dataset,
      final String datasource,
      final EsDocumentService documentService,
      final EsEntityService entityService,
      final String mentionType,
      final String entityType,
      final String relationType) {
    super(dataset, datasource, documentService);
    this.entityService = entityService;
    this.mentionType = mentionType;
    this.entityType = entityType;
    this.relationType = relationType;
  }

  @Override
  public Mono<BaleenDocument> getById(final String id) {
    return getService().getByExternalId(id).map(Converters::toBaleenDocument);
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
  public DocumentSearchResult search(
      final DocumentSearch documentSearch, final int offset, final int limit) {

    final Optional<QueryBuilder> query =
        DocumentFilters.toQuery(documentSearch, mentionType, entityType, relationType);

    Flux<BaleenDocument> results;
    final Mono<Long> total;
    if (query.isPresent()) {
      final Mono<SearchHits<OutputDocument>> hits = getService().search(query.get(), offset, limit);
      results = hits.flatMapMany(SearchHits::getResults).map(Converters::toBaleenDocument);
      total = hits.map(SearchHits::getTotal);
    } else {
      results = getService().getAll(offset, limit).map(Converters::toBaleenDocument);
      total = getService().count();
    }

    return new DocumentSearchResult(results, total, offset, limit);
  }

  @Override
  public Flux<TermBin> countByField(
      final Optional<DocumentFilter> documentFilter, final List<String> path, final int size) {
    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);
    final String field = ElasticsearchMapping.toAggregationField(path);
    if (path.get(0).equalsIgnoreCase(BaleenProperties.METADATA)) {
      return getService().nestedTermAggregation(query, BaleenProperties.METADATA, field, size);
    } else {
      return getService().termAggregation(query, field, size);
    }
  }

  @Override
  public Flux<TimeBin> countByDate(
      final Optional<DocumentFilter> documentFilter, final TimeInterval interval) {
    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);
    final String field =
        ElasticsearchMapping.toAggregationField(
            Arrays.asList(BaleenProperties.PROPERTIES, BaleenProperties.DOCUMENT_DATE));

    return getService().timelineAggregation(query, field, interval);
  }

  @Override
  public Flux<TimeBin> countByJoinedDate(
      final Optional<DocumentFilter> documentFilter,
      final ItemTypes joinedType,
      final TimeInterval interval) {
    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);

    final String type = convertItemToType(joinedType);

    final String field =
        ElasticsearchMapping.toAggregationField(
            Arrays.asList(BaleenProperties.PROPERTIES, BaleenProperties.START_TIMESTAMP));

    final ChildrenAggregationBuilder aggregationBuilder =
        new ChildrenAggregationBuilder(CHILDREN_AGG, type)
            .subAggregation(
                AggregationBuilders.dateHistogram(DH_AGG)
                    .minDocCount(1)
                    .field(field)
                    .dateHistogramInterval(TimeIntervalUtils.toDateHistogram(interval)));

    return getService()
        .aggregation(
            query,
            // NOTE new ... as aggregationbuilders.children does not exist yet!
            aggregationBuilder)
        .flatMapMany(
            agg -> {
              final Children children = agg.get(CHILDREN_AGG);
              final Histogram histogram = children.getAggregations().get(DH_AGG);
              return Flux.fromIterable(histogram.getBuckets())
                  .map(
                      b -> {
                        final Instant i =
                            Instant.ofEpochMilli(((DateTime) b.getKey()).toInstant().getMillis());
                        return new TimeBin(i, b.getDocCount());
                      });
            });
  }

  @Override
  public Flux<TermBin> countByJoinedField(
      final Optional<DocumentFilter> documentFilter,
      final ItemTypes joinedType,
      final List<String> path,
      final int size) {

    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);

    final String type = convertItemToType(joinedType);
    final String field = ElasticsearchMapping.toAggregationField(path);

    final ChildrenAggregationBuilder aggregationBuilder =
        new ChildrenAggregationBuilder(CHILDREN_AGG, type);
    final TermsAggregationBuilder termBuilder =
        AggregationBuilders.terms(TERMS_AGG).field(field).size(size);
    aggregationBuilder.subAggregation(termBuilder);

    return getService()
        .aggregation(
            query,
            // NOTE new ... as aggregationbuilders.children does not exist yet!
            aggregationBuilder)
        .flatMapMany(
            agg -> {
              final Children children = agg.get(CHILDREN_AGG);
              final Terms terms = children.getAggregations().get(TERMS_AGG);
              return Flux.fromIterable(terms.getBuckets())
                  .map(b -> new TermBin(b.getKeyAsString(), b.getDocCount()));
            });
  }

  private String convertItemToType(final ItemTypes type) {
    switch (type) {
      case DOCUMENT:
        return getService().getType();
      case ENTITY:
        return entityType;
      case MENTION:
        return mentionType;
      case RELATION:
        return relationType;
      default:
        throw new IllegalArgumentException("Not a supported join type");
    }
  }

  @Override
  public Flux<NamedGeoLocation> documentLocations(
      final Optional<DocumentFilter> documentFilter, final int size) {
    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);

    final BoolQueryBuilder find =
        QueryBuilders.boolQuery()
            .must(QueryBuilders.typeQuery(entityType))
            .must(QueryBuilders.matchQuery(BaleenProperties.TYPE, BaleenTypes.LOCATION));

    query.ifPresent(
        q -> find.must(JoinQueryBuilders.hasParentQuery(getService().getType(), q, false)));

    return entityService
        .search(find, 0, size)
        .flatMapMany(SearchHits<OutputEntity>::getResults)
        .filter(
            e ->
                e.getProperties() != null
                    && e.getProperties().get("poi") != null
                    && e.getProperties().get("poi") instanceof Collection)
        .flatMap(
            e -> {
              // Checked in the filter
              @SuppressWarnings("unchecked")
              final Collection<Object> pois = (Collection<Object>) e.getProperties().get("poi");

              Mono<NamedGeoLocation> mono = Mono.empty();

              if (pois.size() >= 2) {
                final Collection<Object> c = pois;
                final Iterator<Object> it = c.iterator();
                final Object lon = it.next();
                final Object lat = it.next();

                if (lon instanceof Double && lat instanceof Double && lat != null && lon != null) {
                  mono =
                      Mono.just(
                          new NamedGeoLocation(
                              e.getValue(), e.getType(), (double) lat, (double) lon));
                }
              }
              return mono;
            })
        .distinct()
        .take(size);
  }

  @Override
  public Mono<TimeRange> documentTimeRange(final Optional<DocumentFilter> documentFilter) {
    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);
    return getService()
        .aggregation(
            query,
            AggregationBuilders.min(MIN_AGG)
                .field(BaleenProperties.PROPERTIES + "." + BaleenProperties.DOCUMENT_DATE),
            AggregationBuilders.max(MAX_AGG)
                .field(BaleenProperties.PROPERTIES + "." + BaleenProperties.DOCUMENT_DATE))
        .map(
            agg -> {
              final Min min = agg.get(MIN_AGG);
              final Max max = agg.get(MAX_AGG);
              return new TimeRange(
                  new Date((long) min.getValue()), new Date((long) max.getValue()));
            });
  }

  @Override
  public Mono<TimeRange> entityTimeRange(final Optional<DocumentFilter> documentFilter) {
    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);
    return getService()
        .aggregation(
            query,
            // NOTE new ... as aggregationbuilders.children does not exist yet!
            new ChildrenAggregationBuilder(CHILDREN_AGG, entityType)
                .subAggregation(
                    AggregationBuilders.min(MIN_AGG)
                        .field(
                            BaleenProperties.PROPERTIES + "." + BaleenProperties.START_TIMESTAMP))
                .subAggregation(
                    AggregationBuilders.max(MAX_AGG)
                        .field(
                            BaleenProperties.PROPERTIES + "." + BaleenProperties.STOP_TIMESTAMP)))
        .map(
            agg -> {
              final Children children = agg.get(CHILDREN_AGG);
              final Min min = children.getAggregations().get(MIN_AGG);
              final Max max = children.getAggregations().get(MAX_AGG);
              return new TimeRange(
                  new Date((long) min.getValue()), new Date((long) max.getValue()));
            });
  }
}
