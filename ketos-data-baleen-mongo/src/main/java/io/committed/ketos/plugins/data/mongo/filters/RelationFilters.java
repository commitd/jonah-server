package io.committed.ketos.plugins.data.mongo.filters;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import io.committed.invest.support.mongo.utils.FilterUtils;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.output.RelationSearch;
import io.committed.ketos.plugins.data.mongo.data.CustomFilters;

public final class RelationFilters {

  private RelationFilters() {
    // Singleton
  }

  public static Optional<Bson> createFilter(final Optional<RelationFilter> filter, final boolean operatorMode) {

    if (!filter.isPresent()) {
      return Optional.empty();
    }

    final RelationFilter relationFilter = filter.get();

    final List<Bson> filters = new LinkedList<>();

    if (relationFilter.getId() != null) {
      filters.add(CustomFilters.eqFilter(BaleenProperties.EXTERNAL_ID, relationFilter.getId(),
          operatorMode));
    }

    if (relationFilter.getDocId() != null) {
      filters.add(CustomFilters.eqFilter(BaleenProperties.DOC_ID, relationFilter.getDocId(),
          operatorMode));
    }

    if (relationFilter.getValue() != null) {
      filters.add(Filters.text(relationFilter.getValue()));
    }

    if (relationFilter.getType() != null) {
      filters.add(CustomFilters.eqFilter(BaleenProperties.TYPE, relationFilter.getType(),
          operatorMode));
    }

    if (relationFilter.getSubType() != null) {
      filters.add(CustomFilters.eqFilter(BaleenProperties.SUBTYPE, relationFilter.getSubType(),
          operatorMode));
    }

    if (relationFilter.getProperties() != null) {
      for (final Map.Entry<String, Object> e : relationFilter.getProperties().entrySet()) {
        filters.add(
            CustomFilters.eqFilter(BaleenProperties.PROPERTIES + "." + e.getKey(), e.getValue(),
                operatorMode));
      }
    }


    if (relationFilter.getSource() != null) {
      MentionFilters.createFilter(Optional.ofNullable(relationFilter.getSource()),
          BaleenProperties.RELATION_SOURCE + ".", operatorMode)
          .ifPresent(filters::add);
    }


    if (relationFilter.getTarget() != null) {
      MentionFilters.createFilter(Optional.ofNullable(relationFilter.getTarget()),
          BaleenProperties.RELATION_TARGET + ".", operatorMode)
          .ifPresent(filters::add);

    }


    return FilterUtils.combine(filters);
  }

  public static Stream<Bson> createFilters(final Stream<RelationFilter> filters, final boolean operatorMode) {
    return filters
        .map(f -> createFilter(Optional.ofNullable(f), operatorMode))
        .filter(Optional::isPresent)
        .map(Optional::get);
  }

  public static Optional<Bson> createFilter(final RelationSearch relationSearch) {
    return createFilter(Optional.ofNullable(relationSearch.getRelationFilter()), false);
  }
}
