package io.committed.ketos.data.elasticsearch.providers;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.join.aggregations.Children;
import org.elasticsearch.join.aggregations.ChildrenAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.joda.time.DateTime;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.analytic.TimeRange;
import io.committed.invest.core.dto.constants.TimeInterval;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.invest.support.data.utils.FieldUtils;
import io.committed.invest.support.elasticsearch.utils.TimeIntervalUtils;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.OutputDocument;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.constants.ItemTypes;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.general.NamedGeoLocation;
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

    return new DocumentSearchResult(results, Mono.empty(), offset, limit);
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
    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);

    final String type = convertItemToType(joinedType);
    final ChildrenAggregationBuilder aggregationBuilder =
        new ChildrenAggregationBuilder("children", type).subAggregation(
            AggregationBuilders.nested("nested", BaleenProperties.PROPERTIES)
                .subAggregation(AggregationBuilders.dateHistogram("dh")
                    .minDocCount(1)
                    .dateHistogramInterval(TimeIntervalUtils.toDateHistogram(interval))));


    return getService().aggregation(query,
        // NOTE new ... as aggregationbuilders.children does not exist yet!
        aggregationBuilder)
        .flatMapMany(agg -> {
          final Children children = agg.get("children");
          final Nested nested = children.getAggregations().get("nested");
          final Histogram histogram = nested.getAggregations().get("dh");
          return Flux.fromIterable(histogram.getBuckets()).map(b -> {
            final Instant i = Instant.ofEpochMilli(((DateTime) b.getKey()).toInstant().getMillis());
            return new TimeBin(i, b.getDocCount());
          });
        });
  }

  @Override
  public Flux<TermBin> countByJoinedField(final Optional<DocumentFilter> documentFilter, final ItemTypes joinedType,
      final List<String> path, final int size) {

    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);

    final String type = convertItemToType(joinedType);
    final String field = FieldUtils.joinField(path);


    final ChildrenAggregationBuilder aggregationBuilder = new ChildrenAggregationBuilder("children", type);
    final TermsAggregationBuilder termBuilder = AggregationBuilders.terms("terms").field(field).size(size);

    final boolean isNested = path.size() > 1;

    if (isNested) {
      aggregationBuilder.subAggregation(
          AggregationBuilders.nested("nested", BaleenProperties.PROPERTIES)
              .subAggregation(termBuilder));
    } else {
      aggregationBuilder.subAggregation(termBuilder);
    }

    return getService().aggregation(query,
        // NOTE new ... as aggregationbuilders.children does not exist yet!
        aggregationBuilder)
        .flatMapMany(agg -> {
          final Children children = agg.get("children");
          Terms terms;
          if (isNested) {
            final Nested nested = children.getAggregations().get("nested");
            terms = nested.getAggregations().get("terms");
          } else {
            terms = children.getAggregations().get("terms");
          }
          return Flux.fromIterable(terms.getBuckets()).map(b -> {
            return new TermBin(b.getKeyAsString(), b.getDocCount());
          });
        });


  }

  private String convertItemToType(final ItemTypes type) {
    switch (type) {
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
  public Flux<NamedGeoLocation> documentLocations(final Optional<DocumentFilter> documentFilter, final int size) {
    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);
    // TODO: If we determine any valid in this..
    return Flux.empty();
  }

  @Override
  public Mono<TimeRange> documentTimeRange(final Optional<DocumentFilter> documentFilter) {
    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);
    return getService().aggregation(query,
        AggregationBuilders.nested("nested", BaleenProperties.PROPERTIES)
            .subAggregation(AggregationBuilders.min("min")
                .field(BaleenProperties.PROPERTIES + "." + BaleenProperties.DOCUMENT_DATE))
            .subAggregation(AggregationBuilders.max("max")
                .field(BaleenProperties.PROPERTIES + "." + BaleenProperties.DOCUMENT_DATE)))
        .map(agg -> {
          final Nested nested = agg.get("nested");
          final Min min = nested.getAggregations().get("min");
          final Max max = nested.getAggregations().get("max");
          return new TimeRange(new Date((long) min.getValue()), new Date((long) max.getValue()));
        });
  }

  @Override
  public Mono<TimeRange> entityTimeRange(final Optional<DocumentFilter> documentFilter) {
    final Optional<QueryBuilder> query = DocumentFilters.toQuery(documentFilter);
    return getService().aggregation(query,
        // NOTE new ... as aggregationbuilders.children does not exist yet!
        new ChildrenAggregationBuilder("children", entityType)
            .subAggregation(AggregationBuilders.nested("nested", BaleenProperties.PROPERTIES)
                .subAggregation(AggregationBuilders.min("min")
                    .field(BaleenProperties.PROPERTIES + "." + BaleenProperties.START_TIMESTAMP))
                .subAggregation(AggregationBuilders.max("max")
                    .field(BaleenProperties.PROPERTIES + "." + BaleenProperties.STOP_TIMESTAMP))))
        .map(agg -> {
          final Children children = agg.get("children");
          final Nested nested = children.getAggregations().get("nested");
          final Min min = nested.getAggregations().get("min");
          final Max max = nested.getAggregations().get("max");
          // silly sec to millisec change
          return new TimeRange(new Date((long) min.getValue() * 1000), new Date((long) max.getValue() * 1000));
        });
  }

}

