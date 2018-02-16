package io.committed.ketos.plugins.data.mongo.filters;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.geojson.NamedCoordinateReferenceSystem;
import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.PolygonCoordinates;
import com.mongodb.client.model.geojson.Position;
import io.committed.invest.core.dto.analytic.GeoBox;
import io.committed.invest.support.mongo.utils.FilterUtils;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.output.MentionSearch;
import io.committed.ketos.plugins.data.mongo.data.CustomFilters;

public final class MentionFilters {

  private MentionFilters() {
    // Singleton
  }

  public static Optional<Bson> createFilter(final Optional<MentionFilter> filter, final String prefix,
      final boolean operatorMode) {
    if (!filter.isPresent()) {
      return Optional.empty();
    }

    final MentionFilter mentionFilter = filter.get();

    final List<Bson> filters = new LinkedList<>();

    if (mentionFilter.getId() != null) {
      filters.add(CustomFilters.eqFilter(prefix + BaleenProperties.EXTERNAL_ID, mentionFilter.getId(), operatorMode));
    }

    if (mentionFilter.getDocId() != null) {
      filters.add(CustomFilters.eqFilter(prefix + BaleenProperties.DOC_ID, mentionFilter.getDocId(), operatorMode));
    }

    if (mentionFilter.getEntityId() != null) {
      filters
          .add(CustomFilters.eqFilter(prefix + BaleenProperties.ENTITY_ID, mentionFilter.getEntityId(), operatorMode));
    }

    if (mentionFilter.getType() != null) {
      filters.add(CustomFilters.eqFilter(prefix + BaleenProperties.TYPE, mentionFilter.getType(), operatorMode));
    }

    if (mentionFilter.getValue() != null) {
      filters.add(CustomFilters.eqFilter(prefix + BaleenProperties.VALUE, mentionFilter.getValue(), operatorMode));
    }


    if (mentionFilter.getProperties() != null) {
      for (final Map.Entry<String, Object> e : mentionFilter.getProperties().entrySet()) {
        filters.add(
            CustomFilters.eqFilter(prefix + BaleenProperties.PROPERTIES + "." + e.getKey(), e.getValue(),
                operatorMode));
      }
    }

    if (mentionFilter.getStartTimestamp() != null) {

      filters.add(
          Filters.gte(prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.START_TIMESTAMP,
              mentionFilter.getStartTimestamp().getTime()));
    }


    if (mentionFilter.getEndTimestamp() != null) {
      filters.add(
          Filters.gte(prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.STOP_TIMESTAMP,
              mentionFilter.getEndTimestamp().getTime()));
    }

    if (mentionFilter.getWithin() != null) {
      final GeoBox within = mentionFilter.getWithin();

      // Ideally we'd use a box and within as that's nice and easy...
      // but then we have lots of country stuff which is global (is colonies) so
      // so we need to us an intersection (as box doesn't work with geoJson)

      final Position bl = new Position(within.getSafeW(), within.getSafeS());
      final Position br = new Position(within.getSafeE(), within.getSafeS());
      final Position tr = new Position(within.getSafeE(), within.getSafeN());
      final Position tl = new Position(within.getSafeW(), within.getSafeN());

      // In either within on intersection, this won't actually find anything if the search is bigger
      // than a hemisphere!
      // https://docs.mongodb.com/manual/reference/operator/query/geoIntersects/#intersects-a-big-polygon

      final PolygonCoordinates coordinates = new PolygonCoordinates(Arrays.asList(bl, br, tr, tl, bl));
      final Polygon polygon = new Polygon(NamedCoordinateReferenceSystem.EPSG_4326_STRICT_WINDING, coordinates);
      Filters.geoIntersects(prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.GEOJSON, polygon);
    }


    return FilterUtils.combine(filters);
  }

  public static Optional<Bson> createFilter(final MentionSearch mentionSearch) {
    return createFilter(Optional.ofNullable(mentionSearch.getMentionFilter()), "", false);
  }

  public static Stream<Bson> createFilters(final Stream<MentionFilter> mentionFilters, final boolean operatorMode) {
    return mentionFilters
        .map(f -> createFilter(Optional.ofNullable(f), "", operatorMode))
        .filter(Optional::isPresent)
        .map(Optional::get);
  }
}
