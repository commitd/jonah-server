package io.committed.ketos.data.elasticsearch.filters;

import java.util.Optional;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import io.committed.invest.core.dto.analytic.GeoBox;
import io.committed.invest.core.dto.analytic.GeoRadius;
import io.committed.invest.support.elasticsearch.utils.GeoUtils;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.output.EntitySearch;
import io.committed.ketos.common.utils.ValueConversion;

/**
 * Convert Ketos entity queries to ES queries.
 */
public final class EntityFilters {
  private EntityFilters() {
    // Singleton
  }

  public static Optional<QueryBuilder> toQuery(final EntitySearch entitySearch) {
    final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

    EntityFilters.toQuery(Optional.ofNullable(entitySearch.getEntityFilter()), "")
        .ifPresent(boolQuery::must);

    if (boolQuery.must().isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(boolQuery);
    }
  }

  public static Optional<QueryBuilder> toQuery(final Optional<EntityFilter> entityFilter, final String prefix) {

    if (!entityFilter.isPresent()) {
      return Optional.empty();
    }

    final EntityFilter filter = entityFilter.get();

    final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

    ValueConversion.stringValue(filter.getDocId())
        .map(s -> QueryBuilders.termQuery(prefix + BaleenProperties.DOC_ID, s))
        .ifPresent(queryBuilder::must);

    ValueConversion.stringValue(filter.getId())
        .map(s -> QueryBuilders.termQuery(prefix + BaleenProperties.EXTERNAL_ID, s))
        .ifPresent(queryBuilder::must);

    ValueConversion.stringValue(filter.getType())
        .map(s -> QueryBuilders.matchQuery(prefix + BaleenProperties.TYPE, s))
        .ifPresent(queryBuilder::must);

    ValueConversion.stringValue(filter.getSubType())
        .map(s -> QueryBuilders.matchQuery(prefix + BaleenProperties.SUBTYPE, s))
        .ifPresent(queryBuilder::must);

    ValueConversion.stringValue(filter.getValue())
        .map(s -> QueryBuilders.matchPhraseQuery(prefix + BaleenProperties.VALUE, s))
        .ifPresent(queryBuilder::must);

    ValueConversion.stringValue(filter.getMentionId())
        .map(s -> QueryBuilders.matchQuery(prefix + BaleenProperties.MENTION_IDS, s))
        .ifPresent(queryBuilder::must);

    if (filter.getProperties() != null) {
      filter.getProperties().stream()
          .filter(p -> ValueConversion.isValueOrOther(p.getValue()))
          .map(e -> QueryBuilders.matchQuery(prefix + BaleenProperties.PROPERTIES + "." + e.getKey(),
              ValueConversion.valueOrNull(e.getValue())))
          .forEach(queryBuilder::must);
    }

    if (filter.getStartTimestamp() != null) {
      queryBuilder
          .must(QueryBuilders.rangeQuery(prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.START_TIMESTAMP)
              .gte(filter.getStartTimestamp().getTime()));
    }

    if (filter.getEndTimestamp() != null) {
      queryBuilder
          .must(QueryBuilders.rangeQuery(prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.STOP_TIMESTAMP)
              .lte(filter.getEndTimestamp().getTime()));
    }

    if (filter.getNear() != null) {
      final GeoRadius gr = filter.getNear();
      final String field = prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.GEOJSON;
      GeoUtils.createGeoShapeQuery(field, gr).ifPresent(queryBuilder::must);
    }

    if (filter.getWithin() != null) {
      final GeoBox box = filter.getWithin();
      final String field = prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.GEOJSON;
      GeoUtils.createGeoShapeQuery(field, box).ifPresent(queryBuilder::must);
    }

    return Optional.of(queryBuilder);
  }


}
