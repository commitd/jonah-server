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
      filters.add(Filters.eq(BaleenProperties.DOC_ID, relationFilter.getDocId()));
    }

    if (relationFilter.getValue() != null) {
      filters.add(Filters.text(relationFilter.getValue()));
    }

    if (relationFilter.getType() != null) {
      filters.add(Filters.eq(BaleenProperties.TYPE, relationFilter.getType()));
    }

    if (relationFilter.getSubType() != null) {
      filters.add(Filters.eq(BaleenProperties.SUBTYPE, relationFilter.getSubType()));
    }

    if (relationFilter.getSource() != null) {
      MentionFilters.createFilter(Optional.ofNullable(relationFilter.getSource()),
          BaleenProperties.RELATION_SOURCE + ".")
          .ifPresent(filters::add);
    }


    if (relationFilter.getTarget() != null) {
      MentionFilters.createFilter(Optional.ofNullable(relationFilter.getTarget()),
          BaleenProperties.RELATION_TARGET + ".")
          .ifPresent(filters::add);

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
