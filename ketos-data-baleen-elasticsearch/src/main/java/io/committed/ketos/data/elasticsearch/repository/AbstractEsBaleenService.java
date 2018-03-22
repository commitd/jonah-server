package io.committed.ketos.data.elasticsearch.repository;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.core.constants.TimeInterval;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.support.data.elasticsearch.ElasticsearchSupportService;
import io.committed.invest.support.data.elasticsearch.SearchHits;
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
    return search(QueryBuilders.matchQuery(BaleenProperties.EXTERNAL_ID, id), 0, 1)
        .flatMapMany(SearchHits::getResults)
        .next();
  }

  public Flux<T> getByDocumentId(final String id) {
    return search(QueryBuilders.matchQuery(BaleenProperties.DOC_ID, id), 0, ALL_RESULTS)
        .flatMapMany(SearchHits::getResults);
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

  protected <S> void scroll(final QueryBuilder query, final Class<S> clazz, final BiConsumer<String, S> consumer) {

    SearchResponse scrollResp = getClient().prepareSearch(getIndex())
        .setTypes(getType())
        .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
        .setScroll(new TimeValue(60000))
        .setQuery(query)
        .setSize(100).get();
    do {
      for (final SearchHit hit : scrollResp.getHits().getHits()) {
        final Optional<S> s = convertFromJsonSource(hit.getSourceAsString(), clazz);
        s.ifPresent(v -> consumer.accept(hit.getId(), v));
      }

      scrollResp =
          getClient().prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute()
              .actionGet();
    } while (scrollResp.getHits().getHits().length != 0);

  }


  protected Set<String> findElasticId(final Optional<String> parentType, final Optional<String> parent,
      final String type, final String ourIdField,
      final String ourId) {
    final Set<String> ids = new HashSet<>();

    // Find matches and record id

    final BoolQueryBuilder find = QueryBuilders.boolQuery()
        // .must(QueryBuilders.typeQuery(type))
        .must(QueryBuilders.matchQuery(ourIdField, ourId));

    if (parent.isPresent() && parentType.isPresent()) {
      find.must(JoinQueryBuilders.hasParentQuery(
          parentType.get(),
          // TODO: Hard coded to ExternalId probably ok?
          QueryBuilders.matchQuery(BaleenProperties.EXTERNAL_ID, parent.get()),
          false));
    }

    SearchResponse scrollResp = getClient().prepareSearch(getIndex())

        .setTypes(type)
        .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
        .setScroll(new TimeValue(60000))
        .storedFields("__none__")
        .setQuery(find)
        .setSize(100).get();
    do {
      for (final SearchHit hit : scrollResp.getHits().getHits()) {
        ids.add(hit.getId());
      }

      scrollResp =
          getClient().prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute()
              .actionGet();
    } while (scrollResp.getHits().getHits().length != 0);

    return ids;
  }

  protected <S> Optional<S> convertFromJsonSource(final String json, final Class<S> clazz) {
    try {
      return Optional.ofNullable(getMapper().readValue(json, clazz));
    } catch (final IOException e) {
      log.warn("Ubable to convert to JSON", e);
      return Optional.empty();
    }
  }

  protected Optional<String> convertToJsonSource(final Object t) {
    try {
      return Optional.ofNullable(getMapper().writeValueAsString(t));
    } catch (final JsonProcessingException e) {
      log.warn("Ubable to convert to JSON", e);
      return Optional.empty();
    }
  }



  public boolean updateOrSave(final Optional<String> parentType, final Optional<String> parent, final String idField,
      final String id, final T t) {

    // The initial implementation used UpdateByQuery to attempt to replace the _source
    // but that didn't work.
    // The new implemenation will just file all the items which match and then get the elastic
    // ids from that in order to force a update index.

    final Set<String> ids = findElasticId(parentType, parent, getType(), idField, id);

    // Update

    final Optional<String> source = convertToJsonSource(t);

    if (!source.isPresent()) {
      return false;
    }

    final String sourceValue = source.get();

    // TODO: We could use a bulk index here

    if (!ids.isEmpty()) {
      boolean success = true;
      final String parentId = parent.orElse(null);
      for (final String i : ids) {
        success &= update(parentId, i, getType(), sourceValue);
      }
      return success;
    } else {
      return save(parent.orElse(null), getType(), sourceValue);
    }

  }

  protected boolean save(@Nullable final String parent, final String type, final Object o) {
    final Optional<String> optional = convertToJsonSource(o);
    if (!optional.isPresent()) {
      return false;
    }

    return save(parent, type, optional.get());
  }

  protected boolean save(@Nullable final String parent, final String type,
      final String value) {
    try {

      final IndexRequestBuilder builder = getClient().prepareIndex(getIndex(), type)
          .setSource(value, XContentType.JSON);

      if (parent != null) {
        builder.setParent(parent);
      }

      final IndexResponse indexResponse = builder.execute()
          .get();
      return indexResponse.status().equals(RestStatus.OK);
    } catch (final Exception e) {
      log.warn("Ubable to execute index", e);
      return false;
    }
  }

  protected boolean update(@Nullable final String parent, final String id, final String type,
      final Object o) {
    final Optional<String> optional = convertToJsonSource(o);
    if (!optional.isPresent()) {
      return false;
    }

    return update(parent, id, type, optional.get());
  }

  protected boolean update(@Nullable final String parent, final String id, final String type,
      final String value) {
    try {

      final IndexRequestBuilder builder = getClient().prepareIndex(getIndex(), type, id)
          .setSource(value, XContentType.JSON);

      if (parent != null) {
        builder.setParent(parent);
      }

      final IndexResponse indexResponse = builder.execute()
          .get();
      return indexResponse.status().equals(RestStatus.OK);
    } catch (final Exception e) {
      log.warn("Ubable to execute update", e);
      return false;
    }
  }
}
