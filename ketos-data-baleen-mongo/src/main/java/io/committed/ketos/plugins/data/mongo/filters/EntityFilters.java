package io.committed.ketos.plugins.data.mongo.filters;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.geojson.NamedCoordinateReferenceSystem;
import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.PolygonCoordinates;
import com.mongodb.client.model.geojson.Position;
import io.committed.invest.core.dto.analytic.GeoBox;
import io.committed.invest.support.mongo.utils.FilterUtils;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.output.EntitySearch;

public final class EntityFilters {

  private EntityFilters() {
    // Singleton
  }

  public static Optional<Bson> createFilter(final Optional<EntityFilter> filter, final String prefix) {

    if (!filter.isPresent()) {
      return Optional.empty();
    }

    final EntityFilter mentionFilter = filter.get();

    final List<Bson> filters = new LinkedList<>();


    if (mentionFilter.getId() != null) {
      filters.add(Filters.eq(prefix + BaleenProperties.EXTERNAL_ID, mentionFilter.getId()));
    }

    if (mentionFilter.getDocId() != null) {
      filters.add(Filters.eq(prefix + BaleenProperties.DOC_ID, mentionFilter.getDocId()));
    }

    if (mentionFilter.getMentionId() != null) {
      filters.add(Filters.eq(prefix + BaleenProperties.MENTION_IDS, mentionFilter.getMentionId()));
    }

    if (mentionFilter.getType() != null) {
      filters.add(Filters.eq(prefix + BaleenProperties.TYPE, mentionFilter.getType()));
    }

    if (mentionFilter.getValue() != null) {
      filters.add(Filters.eq(prefix + BaleenProperties.VALUE, mentionFilter.getValue()));
    }


    if (mentionFilter.getProperties() != null) {
      for (final Map.Entry<String, Object> e : mentionFilter.getProperties().entrySet()) {
        filters.add(
            Filters.eq(prefix + BaleenProperties.PROPERTIES + "." + e.getKey(), e.getValue()));
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

  public static Optional<Bson> createFilter(final EntitySearch entitySearch) {
    return createFilter(Optional.ofNullable(entitySearch.getEntityFilter()), "");
  }


}
