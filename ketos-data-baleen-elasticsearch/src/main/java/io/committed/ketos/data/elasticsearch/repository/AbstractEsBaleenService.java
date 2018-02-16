package io.committed.ketos.data.elasticsearch.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.joda.time.DateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.constants.TimeInterval;
import io.committed.invest.support.data.elasticsearch.ElasticsearchSupportService;
import io.committed.invest.support.data.utils.FieldUtils;
import io.committed.invest.support.elasticsearch.utils.TimeIntervalUtils;
import io.committed.ketos.common.constants.BaleenProperties;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractEsBaleenService<T> extends ElasticsearchSupportService<T> {

  // We have to provide an upper value...
  private static final int ALL_RESULTS = 10000;

  public AbstractEsBaleenService(final Client client, final ObjectMapper mapper, final String indexName,
      final String typeName, final Class<T> clazz) {
    super(client, mapper, indexName, typeName, clazz);
  }

  public Mono<T> getByExternalId(final String id) {
    return search(QueryBuilders.matchQuery(BaleenProperties.EXTERNAL_ID, id), 0, 1).next();
  }

  public Flux<T> getByDocumentId(final String id) {
    return search(QueryBuilders.matchQuery(BaleenProperties.DOC_ID, id), 0, ALL_RESULTS);
  }


  public Flux<TermBin> termAggregation(final Optional<QueryBuilder> query, final List<String> path,
      final int size) {
    final String field = FieldUtils.joinField(path);
    return termAggregation(query, field, size);
  }

  public Flux<TermBin> nestedTermAggregation(final Optional<QueryBuilder> query,
      final String nestedPath, final String field, final int size) {

    final NestedAggregationBuilder aggregationBuilder =
        AggregationBuilders.nested("nested", nestedPath)
            .subAggregation(AggregationBuilders.terms("agg")
                .field(field)
                .size(size));


    return aggregation(query, aggregationBuilder)
        .flatMapMany(as -> {
          final Nested nested = as.get("nested");
          final Terms terms = nested.getAggregations().get("agg");
          return Flux.fromIterable(terms.getBuckets()).map(b -> {
            return new TermBin(b.getKeyAsString(), b.getDocCount());
          });

        });
  }

  public Flux<TimeBin> nestedTimelineAggregation(final Optional<QueryBuilder> query, final TimeInterval interval,
      final String nestedPath, final String field) {

    final NestedAggregationBuilder aggregationBuilder =
        AggregationBuilders.nested("nested", nestedPath)
            .subAggregation(AggregationBuilders.dateHistogram("agg")
                .field(field)
                .dateHistogramInterval(TimeIntervalUtils.toDateHistogram(interval))
                .minDocCount(1));


    return aggregation(query, aggregationBuilder)
        .flatMapMany(as -> {
          final Nested nested = as.get("nested");
          final Histogram terms = nested.getAggregations().get("agg");
          return Flux.fromIterable(terms.getBuckets()).map(b -> {
            final Instant i = Instant.ofEpochMilli(((DateTime) b.getKey()).toInstant().getMillis());
            return new TimeBin(i, b.getDocCount());
          });

        });
  }
}
