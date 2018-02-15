package io.committed.ketos.data.elasticsearch.filters;

import java.util.Optional;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.output.EntitySearch;

public final class EntityFilters {
  private EntityFilters() {
    // Singleton
  }

  public static Optional<QueryBuilder> toQuery(final EntitySearch entitySearch) {
    final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

    EntityFilters.toQuery(Optional.ofNullable(entitySearch.getEntityFilter()), "")
        .ifPresent(boolQuery::must);

    entitySearch.getMentionFilters().stream()
        .map(f -> MentionFilters.toMentionsQuery(f, ""))
        .filter(Optional::isPresent)
        .forEach(q -> boolQuery.must(q.get()));

    if (boolQuery.must().isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(boolQuery);
    }
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
