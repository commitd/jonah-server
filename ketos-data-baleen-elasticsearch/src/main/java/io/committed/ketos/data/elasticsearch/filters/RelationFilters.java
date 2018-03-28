package io.committed.ketos.data.elasticsearch.filters;

import java.util.Optional;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.output.RelationSearch;
import io.committed.ketos.common.utils.ValueConversion;

/** Convert Ketos relation queries to ES queries. */
public final class RelationFilters {
  private RelationFilters() {
    // Singleton
  }

  public static Optional<QueryBuilder> toQuery(
      final Optional<RelationFilter> relationFilter, final String prefix) {
    if (!relationFilter.isPresent()) {
      return Optional.empty();
    }

    final RelationFilter filter = relationFilter.get();

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

    if (filter.getProperties() != null) {
      filter
          .getProperties()
          .stream()
          .filter(p -> ValueConversion.isValueOrOther(p.getValue()))
          .map(
              e ->
                  QueryBuilders.matchQuery(
                      prefix + BaleenProperties.PROPERTIES + "." + e.getKey(),
                      ValueConversion.valueOrNull(e.getValue())))
          .forEach(queryBuilder::must);
    }

    if (filter.getSource() != null) {
      MentionFilters.toQuery(
              Optional.ofNullable(filter.getSource()),
              prefix + BaleenProperties.RELATION_SOURCE + ".")
          .ifPresent(queryBuilder::must);
    }

    if (filter.getTarget() != null) {
      MentionFilters.toQuery(
              Optional.ofNullable(filter.getTarget()),
              prefix + BaleenProperties.RELATION_TARGET + ".")
          .ifPresent(queryBuilder::must);
    }

    return Optional.of(queryBuilder);
  }

  public static Optional<QueryBuilder> toQuery(final RelationSearch search) {
    return toQuery(Optional.ofNullable(search.getRelationFilter()), "");
  }
}
