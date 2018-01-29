package io.committed.ketos.data.elasticsearch.filters;

import java.util.Optional;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import io.committed.ketos.common.graphql.input.RelationFilter;

public final class RelationFilters {
  private RelationFilters() {
    // Singleton
  }

  public static Optional<QueryBuilder> toQuery(final Optional<RelationFilter> filter, final String prefix) {
    return filter.flatMap(f -> toQuery(f, prefix));
  }

  public static Optional<QueryBuilder> toQuery(final RelationFilter filter, final String prefix) {
    final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

    if (filter.getDocId() != null) {
      // This is on the .externalId not entities.docId, so no prefix
      queryBuilder.must(QueryBuilders.termQuery("docId", filter.getDocId()));
    }

    if (filter.getId() != null) {
      queryBuilder.must(QueryBuilders.termQuery(prefix + "externalId", filter.getId()));
    }

    if (filter.getRelationshipType() != null) {
      queryBuilder.must(QueryBuilders.matchQuery(prefix + "relationshipType", filter.getRelationshipType()));
    }

    if (filter.getRelationSubtype() != null) {
      queryBuilder.must(QueryBuilders.matchQuery(prefix + "relationSubtype", filter.getRelationSubtype()));
    }

    if (filter.getSourceId() != null) {
      queryBuilder.must(QueryBuilders.termQuery(prefix + "source", filter.getSourceId()));
    }

    if (filter.getTargetId() != null) {
      queryBuilder.must(QueryBuilders.termQuery(prefix + "target", filter.getSourceId()));
    }

    if (filter.getType() != null) {
      queryBuilder.must(QueryBuilders.matchQuery(prefix + "type", filter.getType()));
    }

    if (filter.getValue() != null) {
      queryBuilder.must(QueryBuilders.matchQuery(prefix + "value", filter.getValue()));
    }


    // TODO: Source/target type/value are not supported...
    // but we could actually look for them on the entities after we return (in the RelationProvider
    // rather than here). That wouldn't help us with some thing (like aggregations)
    // Of course we could add value to the ES output


    return Optional.of(queryBuilder);
  }
}
