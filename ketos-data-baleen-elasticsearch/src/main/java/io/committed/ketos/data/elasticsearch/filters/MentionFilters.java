package io.committed.ketos.data.elasticsearch.filters;

import java.util.Optional;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import io.committed.invest.core.dto.analytic.GeoBox;
import io.committed.ketos.common.graphql.input.MentionFilter;

public final class MentionFilters {
  private MentionFilters() {
    // Singleton
  }

  public static Optional<QueryBuilder> toQuery(final Optional<MentionFilter> filter, final String prefix) {
    return filter.flatMap(f -> toQuery(f, prefix));
  }

  public static Optional<QueryBuilder> toQuery(final MentionFilter filter, final String prefix) {
    final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

    if (filter.getDocId() != null) {
      // This is on the .externalId not entities.docId, so no prefix
      queryBuilder.must(QueryBuilders.termQuery("docId", filter.getDocId()));
    }

    if (filter.getEndTimestamp() != null) {
      // TODO: verify this is the right name (and then change getStartTimestamp if not)
      queryBuilder.must(QueryBuilders.rangeQuery("timestampEnd").lte(filter.getEndTimestamp()));
    }

    if (filter.getEntityId() != null) {
      // Actually no such thing... but mention-entity id
      queryBuilder.must(QueryBuilders.termQuery(prefix + "externalId", filter.getEntityId()));

    }

    if (filter.getId() != null) {
      queryBuilder.must(QueryBuilders.termQuery(prefix + "externalId", filter.getId()));

    }

    if (filter.getProperties() != null) {
      filter.getProperties().entrySet()
          .forEach(e -> queryBuilder.must(QueryBuilders.matchQuery(prefix + e.getKey(), e.getValue())));
    }

    if (filter.getStartTimestamp() != null) {
      queryBuilder.must(QueryBuilders.rangeQuery("timestampStart").gte(filter.getStartTimestamp()));

    }

    if (filter.getType() != null) {
      queryBuilder.must(QueryBuilders.matchQuery(prefix + "type", filter.getType()));

    }

    if (filter.getValue() != null) {
      queryBuilder.must(QueryBuilders.matchQuery(prefix + "value", filter.getValue()));

    }

    if (filter.getWithin() != null) {
      final GeoBox box = filter.getWithin();
      queryBuilder.must(QueryBuilders.geoBoundingBoxQuery("geoJson")
          .setCorners(box.getSafeN(), box.getSafeW(), box.getSafeS(), box.getSafeE()));

    }

    return Optional.of(queryBuilder);
  }
}
