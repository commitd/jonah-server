package io.committed.ketos.plugins.data.mongo.filters;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import io.committed.invest.support.mongo.utils.FilterUtils;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.output.RelationSearch;

public final class RelationFilters {

  private RelationFilters() {
    // Singleton
  }

  public static Optional<Bson> createFilter(final Optional<RelationFilter> filter) {

    if (!filter.isPresent()) {
      return Optional.empty();
    }

    final RelationFilter relationFilter = filter.get();

    final List<Bson> filters = new LinkedList<>();

    if (relationFilter.getId() != null) {
      filters.add(Filters.eq(BaleenProperties.EXTERNAL_ID, relationFilter.getId()));
    }

    if (relationFilter.getDocId() != null) {
      filters.add(Filters.eq(BaleenProperties.DOC_ID, relationFilter.getRelationshipType()));
    }

    if (relationFilter.getValue() != null) {
      filters.add(Filters.text(relationFilter.getValue()));
    }

    // relationtype has dropped so map it and getType to the same thing
    final String relationType = relationFilter.getRelationshipType() != null ? relationFilter.getRelationshipType()
        : relationFilter.getType();

    if (relationType != null) {
      filters.add(Filters.eq(BaleenProperties.TYPE, relationType));
    }

    if (relationFilter.getRelationSubtype() != null) {
      filters.add(Filters.eq(BaleenProperties.SUBTYPE, relationFilter.getRelationSubtype()));
    }

    if (relationFilter.getSourceId() != null) {
      filters.add(Filters.eq(BaleenProperties.RELATION_SOURCE + "." + BaleenProperties.EXTERNAL_ID,
          relationFilter.getSourceId()));
    }

    if (relationFilter.getSourceType() != null) {
      filters.add(
          Filters.eq(BaleenProperties.RELATION_SOURCE + "." + BaleenProperties.TYPE, relationFilter.getSourceType()));
    }

    if (relationFilter.getSourceValue() != null) {
      filters.add(
          Filters.eq(BaleenProperties.RELATION_SOURCE + "." + BaleenProperties.VALUE, relationFilter.getSourceValue()));
    }

    if (relationFilter.getTargetId() != null) {
      filters
          .add(Filters.eq(BaleenProperties.RELATION_TARGET + "." + BaleenProperties.EXTERNAL_ID,
              relationFilter.getTargetId()));
    }


    if (relationFilter.getTargetType() != null) {
      filters.add(
          Filters.eq(BaleenProperties.RELATION_TARGET + "." + BaleenProperties.TYPE, relationFilter.getTargetType()));
    }

    if (relationFilter.getTargetValue() != null) {
      filters.add(
          Filters.eq(BaleenProperties.RELATION_TARGET + "." + BaleenProperties.VALUE, relationFilter.getTargetValue()));
    }

    return FilterUtils.combine(filters);
  }

  public static Stream<Bson> createFilters(final Stream<RelationFilter> filters) {
    return filters
        .map(f -> createFilter(Optional.ofNullable(f)))
        .filter(Optional::isPresent)
        .map(Optional::get);
  }

  public static Optional<Bson> createFilter(final RelationSearch relationSearch) {
    return createFilter(Optional.ofNullable(relationSearch.getRelationFilter()));
  }
}
