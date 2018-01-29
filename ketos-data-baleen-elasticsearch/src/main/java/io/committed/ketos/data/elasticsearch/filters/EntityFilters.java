package io.committed.ketos.data.elasticsearch.filters;

import java.util.Optional;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import io.committed.ketos.common.graphql.input.EntityFilter;

public final class EntityFilters {
  private EntityFilters() {
    // Singleton
  }

  public static Optional<QueryBuilder> toQuery(final Optional<EntityFilter> filter, final String prefix) {
    return filter.flatMap(f -> toQuery(f, prefix));
  }

  public static Optional<QueryBuilder> toQuery(final EntityFilter filter, final String prefix) {
    final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

    if (filter.getId() != null) {
      // Actually no such thing... but mention-entity id
      queryBuilder.must(QueryBuilders.termQuery(prefix + "externalId", filter.getId()));

    }

    if (filter.getDocId() != null) {
      // This is on the root, so no prefix
      queryBuilder.must(QueryBuilders.termQuery("docId", filter.getDocId()));
    }

    return Optional.of(queryBuilder);
  }
}
