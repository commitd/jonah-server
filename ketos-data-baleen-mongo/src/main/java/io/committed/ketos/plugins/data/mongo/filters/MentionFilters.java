package io.committed.ketos.plugins.data.mongo.filters;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.geojson.NamedCoordinateReferenceSystem;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.PolygonCoordinates;
import com.mongodb.client.model.geojson.Position;

import io.committed.invest.core.dto.analytic.GeoBox;
import io.committed.invest.core.dto.analytic.GeoRadius;
import io.committed.invest.support.mongo.utils.FilterUtils;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.output.MentionSearch;
import io.committed.ketos.common.utils.ValueConversion;
import io.committed.ketos.plugins.data.mongo.data.CustomFilters;

/** Convert Ketos mention queries to Mongo queries. */
public final class MentionFilters {

  private MentionFilters() {
    // Singleton
  }

  public static Optional<Bson> createFilter(
      final Optional<MentionFilter> filter, final String prefix, final boolean operatorMode) {
    if (!filter.isPresent()) {
      return Optional.empty();
    }

    final MentionFilter mentionFilter = filter.get();

    final List<Bson> filters = new LinkedList<>();

    ValueConversion.stringValue(mentionFilter.getValue())
        .map(Filters::text)
        .ifPresent(filters::add);

    ValueConversion.stringValue(mentionFilter.getId())
        .map(s -> CustomFilters.eqFilter(prefix + BaleenProperties.EXTERNAL_ID, s, operatorMode))
        .ifPresent(filters::add);

    ValueConversion.stringValue(mentionFilter.getDocId())
        .map(s -> CustomFilters.eqFilter(prefix + BaleenProperties.DOC_ID, s, operatorMode))
        .ifPresent(filters::add);

    ValueConversion.stringValue(mentionFilter.getEntityId())
        .map(s -> CustomFilters.eqFilter(prefix + BaleenProperties.ENTITY_ID, s, operatorMode))
        .ifPresent(filters::add);

    ValueConversion.stringValue(mentionFilter.getType())
        .map(s -> CustomFilters.eqFilter(prefix + BaleenProperties.TYPE, s, operatorMode))
        .ifPresent(filters::add);

    ValueConversion.stringValue(mentionFilter.getSubType())
        .map(s -> CustomFilters.eqFilter(prefix + BaleenProperties.SUBTYPE, s, operatorMode))
        .ifPresent(filters::add);

    if (mentionFilter.getProperties() != null) {
      mentionFilter
          .getProperties()
          .stream()
          .filter(p -> ValueConversion.isValueOrOther(p.getValue()))
          .map(
              e ->
                  CustomFilters.eqFilter(
                      prefix + BaleenProperties.PROPERTIES + "." + e.getKey(),
                      ValueConversion.valueOrNull(e.getValue()),
                      operatorMode))
          .forEach(filters::add);
    }

    if (mentionFilter.getStartTimestamp() != null) {

      filters.add(
          Filters.gte(
              prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.START_TIMESTAMP,
              mentionFilter.getStartTimestamp().getTime()));
    }

    if (mentionFilter.getEndTimestamp() != null) {
      filters.add(
          Filters.gte(
              prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.STOP_TIMESTAMP,
              mentionFilter.getEndTimestamp().getTime()));
    }

    if (mentionFilter.getNear() != null && !operatorMode) {
      final GeoRadius near = mentionFilter.getNear();

      final Point p = new Point(new Position(near.getLon(), near.getLat()));
      filters.add(
          Filters.near(
              prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.GEOJSON,
              p,
              near.getRadius(),
              null));
    }

    // Doesn't work in aggregation
    if (mentionFilter.getWithin() != null && !operatorMode) {
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

      final PolygonCoordinates coordinates =
          new PolygonCoordinates(Arrays.asList(bl, br, tr, tl, bl));
      final Polygon polygon =
          new Polygon(NamedCoordinateReferenceSystem.EPSG_4326_STRICT_WINDING, coordinates);
      filters.add(
          Filters.geoWithin(
              prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.GEOJSON, polygon));
    }

    return FilterUtils.combine(filters);
  }

  public static Optional<Bson> createFilter(final MentionSearch mentionSearch) {
    return createFilter(Optional.ofNullable(mentionSearch.getMentionFilter()), "", false);
  }

  public static Stream<Bson> createFilters(
      final Stream<MentionFilter> mentionFilters, final boolean operatorMode) {
    return mentionFilters
        .map(f -> createFilter(Optional.ofNullable(f), "", operatorMode))
        .filter(Optional::isPresent)
        .map(Optional::get);
  }
}
