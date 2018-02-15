package io.committed.ketos.data.elasticsearch.filters;

import java.util.Optional;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import io.committed.invest.core.dto.analytic.GeoBox;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.output.MentionSearch;

public final class MentionFilters {
  private MentionFilters() {
    // Singleton
  }

  public static Optional<QueryBuilder> toQuery(final Optional<MentionFilter> mentionFilter,
      final String prefix) {

    if (!mentionFilter.isPresent()) {
      return Optional.empty();
    }

    final MentionFilter filter = mentionFilter.get();

    final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();



    if (filter.getEntityId() != null) {
      // Actually no such thing... but mention-entity id
      queryBuilder.must(QueryBuilders.termQuery(prefix + BaleenProperties.ENTITY_ID, filter.getEntityId()));

    }

    if (filter.getId() != null) {
      queryBuilder.must(QueryBuilders.termQuery(prefix + BaleenProperties.EXTERNAL_ID, filter.getId()));

    }

    if (filter.getProperties() != null) {
      filter.getProperties().entrySet()
          .forEach(e -> queryBuilder
              .must(QueryBuilders.matchQuery(prefix + BaleenProperties.PROPERTIES + "." + e.getKey(), e.getValue())));
    }

    if (filter.getStartTimestamp() != null) {
      queryBuilder
          .must(QueryBuilders.rangeQuery(prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.START_TIMESTAMP)
              .gte(filter.getStartTimestamp()));
    }

    if (filter.getEndTimestamp() != null) {
      queryBuilder
          .must(QueryBuilders.rangeQuery(prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.STOP_TIMESTAMP)
              .lte(filter.getEndTimestamp()));
    }

    if (filter.getType() != null) {
      queryBuilder.must(QueryBuilders.matchQuery(prefix + BaleenProperties.TYPE, filter.getType()));

    }

    if (filter.getSubType() != null) {
      queryBuilder.must(QueryBuilders.matchQuery(prefix + BaleenProperties.SUBTYPE, filter.getSubType()));

    }

    if (filter.getValue() != null) {
      queryBuilder.must(QueryBuilders.matchPhraseQuery(prefix + BaleenProperties.VALUE, filter.getValue()));

    }

    if (filter.getWithin() != null) {
      final GeoBox box = filter.getWithin();
      queryBuilder
          .must(QueryBuilders.geoBoundingBoxQuery(prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.GEOJSON)
              .setCorners(box.getSafeN(), box.getSafeW(), box.getSafeS(), box.getSafeE()));
    }

    return Optional.of(queryBuilder);
  }

  public static Optional<QueryBuilder> toQuery(final MentionSearch search) {
    return toQuery(Optional.ofNullable(search.getMentionFilter()), "");
  }
}
