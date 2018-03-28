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
import io.committed.ketos.common.utils.ValueConversion;
import io.committed.ketos.plugins.data.mongo.data.CustomFilters;

/**
 * Convert Ketos relation queries to Mongo queries.
 */
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

    ValueConversion.stringValue(relationFilter.getValue())
        .map(Filters::text)
        .ifPresent(filters::add);

    ValueConversion.stringValue(relationFilter.getId())
        .map(s -> CustomFilters.eqFilter(BaleenProperties.EXTERNAL_ID, s, operatorMode))
        .ifPresent(filters::add);

    ValueConversion.stringValue(relationFilter.getDocId())
        .map(s -> CustomFilters.eqFilter(BaleenProperties.DOC_ID, s, operatorMode))
        .ifPresent(filters::add);

    ValueConversion.stringValue(relationFilter.getType())
        .map(s -> CustomFilters.eqFilter(BaleenProperties.TYPE, s, operatorMode))
        .ifPresent(filters::add);

    ValueConversion.stringValue(relationFilter.getSubType())
        .map(s -> CustomFilters.eqFilter(BaleenProperties.SUBTYPE, s, operatorMode))
        .ifPresent(filters::add);

    if (relationFilter.getProperties() != null) {
      relationFilter.getProperties().stream()
          .filter(p -> ValueConversion.isValueOrOther(p.getValue()))
          .map(e -> CustomFilters.eqFilter(BaleenProperties.PROPERTIES + "." + e.getKey(),
              ValueConversion.valueOrNull(e.getValue()),
              operatorMode))
          .forEach(filters::add);
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
