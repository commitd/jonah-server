package io.committed.ketos.plugins.data.mongo.filters;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.springframework.data.mongodb.core.query.Criteria;
import io.committed.ketos.common.graphql.input.RelationFilter;

public final class RelationFilters {

  private RelationFilters() {
    // Singleton
  }

  public static List<Criteria> createCriteria(final Collection<RelationFilter> relationFilters,
      final String prefix) {
    final List<Criteria> list = new LinkedList<>();
    for (final RelationFilter f : relationFilters) {
      list.add(createCriteria(f, prefix));
    }
    return list;
  }

  public static Criteria createCriteria(final RelationFilter relationFilter) {
    return createCriteria(relationFilter, "");
  }

  public static Criteria createCriteria(final RelationFilter relationFilter, final String prefix) {
    Criteria criteria = new Criteria();

    if (relationFilter.getDocId() != null) {
      criteria = criteria.and(prefix + "docId").is(relationFilter.getDocId());
    }

    if (relationFilter.getType() != null) {
      criteria = criteria.and(prefix + "type").is(relationFilter.getType());
    }

    if (relationFilter.getValue() != null) {
      criteria = criteria.and(prefix + "value").is(relationFilter.getValue());
    }

    if (relationFilter.getRelationshipType() != null) {
      criteria = criteria.and(prefix + "relationshipType").is(relationFilter.getRelationshipType());
    }

    if (relationFilter.getRelationSubtype() != null) {
      criteria = criteria.and(prefix + "relationSubtype").is(relationFilter.getRelationSubtype());
    }

    if (relationFilter.getSourceId() != null) {
      criteria = criteria.and(prefix + "sourceId").is(relationFilter.getSourceId());
    }

    if (relationFilter.getSourceType() != null) {
      criteria = criteria.and(prefix + "sourceType").is(relationFilter.getSourceType());
    }

    if (relationFilter.getSourceValue() != null) {
      criteria = criteria.and(prefix + "sourceValue").is(relationFilter.getSourceValue());
    }

    if (relationFilter.getTargetId() != null) {
      criteria = criteria.and(prefix + "targetId").is(relationFilter.getTargetId());
    }


    if (relationFilter.getTargetType() != null) {
      criteria = criteria.and(prefix + "targetType").is(relationFilter.getTargetType());
    }

    if (relationFilter.getTargetValue() != null) {
      criteria = criteria.and(prefix + "targetValue").is(relationFilter.getTargetValue());
    }

    return criteria;
  }
}
