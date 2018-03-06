package io.committed.ketos.data.elasticsearch.repository;

import java.time.Instant;
import java.util.Optional;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.joda.time.DateTime;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.constants.TimeInterval;
import io.committed.invest.support.data.elasticsearch.ElasticsearchSupportService;
import io.committed.invest.support.elasticsearch.utils.TimeIntervalUtils;
import io.committed.ketos.common.constants.BaleenProperties;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
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


  // public Flux<TermBin> termAggregation(final Optional<QueryBuilder> query, final List<String> path,
  // final int size) {
  // final String field = FieldUtils.joinField(path);
  // if (path.size() == 1) {
  // return termAggregation(query, field, size);
  // } else {
  // return nestedTermAggregation(query, path.get(0), field, size);
  //
  // }
  // }

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

  public boolean updateOrSave(final String idField, final String id, final T t) {
    String value;
    try {
      value = getMapper().writeValueAsString(t);
    } catch (final JsonProcessingException e) {
      log.warn("UNable to save", e);
      return false;
    }
    final Script script = new Script(String.format("ctx._source = %s", value));

    final UpdateByQueryRequestBuilder ubqrb = UpdateByQueryAction.INSTANCE
        .newRequestBuilder(getClient())
        .source(getIndex())
        .script(script)
        .filter(QueryBuilders.boolQuery()
            .must(QueryBuilders.typeQuery(getType()))
            .must(QueryBuilders.matchQuery(idField, id)));


    BulkByScrollResponse bulkByScrollResponse;
    try {
      bulkByScrollResponse = ubqrb.execute().get();
    } catch (final Exception e) {
      log.warn("Ubable to execute update", e);
      return false;
    }

    if (bulkByScrollResponse.getUpdated() > 0) {
      return true;
    }

    // Not updated any, so create new
    try {

      final IndexResponse indexResponse = getClient().prepareIndex(getIndex(), getType())
          .setSource(value, XContentType.JSON)
          .execute()
          .get();
      return indexResponse.status().equals(RestStatus.OK);
    } catch (final Exception e) {
      log.warn("Ubable to execute index", e);
      return false;
    }
  }
}
