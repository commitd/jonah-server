package io.committed.ketos.data.elasticsearch.filters;

import java.util.Optional;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.output.RelationSearch;

public final class RelationFilters {
  private RelationFilters() {
    // Singleton
  }

  public static Optional<QueryBuilder> toQuery(final Optional<RelationFilter> relationFilter, final String prefix) {
    if (!relationFilter.isPresent()) {
      return Optional.empty();
    }

    final RelationFilter filter = relationFilter.get();

    final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

    if (filter.getDocId() != null) {
      // This is on the .externalId not entities.docId, so no prefix
      queryBuilder.must(QueryBuilders.termQuery(prefix + BaleenProperties.DOC_ID, filter.getDocId()));
    }

    if (filter.getId() != null) {
      queryBuilder.must(QueryBuilders.termQuery(prefix + BaleenProperties.EXTERNAL_ID, filter.getId()));
    }

    if (filter.getSubType() != null) {
      queryBuilder.must(QueryBuilders.matchQuery(prefix + BaleenProperties.SUBTYPE, filter.getSubType()));
    }

    if (filter.getSource() != null) {
      MentionFilters
          .toQuery(Optional.ofNullable(filter.getSource()), prefix + BaleenProperties.RELATION_SOURCE + ".")
          .ifPresent(queryBuilder::must);
    }

    if (filter.getTarget() != null) {
      MentionFilters
          .toQuery(Optional.ofNullable(filter.getTarget()), prefix + BaleenProperties.RELATION_TARGET + ".")
          .ifPresent(queryBuilder::must);
    }

    if (filter.getType() != null) {
      queryBuilder.must(QueryBuilders.matchQuery(prefix + BaleenProperties.TYPE, filter.getType()));
    }

    if (filter.getValue() != null) {
      queryBuilder.must(QueryBuilders.matchPhraseQuery(prefix + BaleenProperties.VALUE, filter.getValue()));
    }

    if (filter.getProperties() != null) {
      filter.getProperties().stream()
          .forEach(e -> queryBuilder
              .must(QueryBuilders.matchQuery(prefix + BaleenProperties.PROPERTIES + "." + e.getKey(), e.getValue())));
    }

    return Optional.of(queryBuilder);
  }

  public static Optional<QueryBuilder> toQuery(final RelationSearch search) {
    return toQuery(Optional.ofNullable(search.getRelationFilter()), "");
  }
}
