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
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.output.EntitySearch;
import io.committed.ketos.plugins.data.mongo.data.CustomFilters;

public final class EntityFilters {

  private EntityFilters() {
    // Singleton
  }

  public static Optional<Bson> createFilter(final Optional<EntityFilter> filter, final String prefix,
      final boolean operatorMode) {

    if (!filter.isPresent()) {
      return Optional.empty();
    }

    final EntityFilter entityFilter = filter.get();

    final List<Bson> filters = new LinkedList<>();

    if (entityFilter.getValue() != null) {
      // Since there is only a single text index, we can't be specific here about which value to use
      // prefix + BaleenProperties.VALUE, ;
      filters.add(Filters.text(entityFilter.getValue()));
    }

    if (entityFilter.getId() != null) {
      filters.add(CustomFilters.eqFilter(prefix + BaleenProperties.EXTERNAL_ID, entityFilter.getId(), operatorMode));
    }

    if (entityFilter.getDocId() != null) {
      filters.add(CustomFilters.eqFilter(prefix + BaleenProperties.DOC_ID, entityFilter.getDocId(), operatorMode));
    }

    if (entityFilter.getMentionId() != null) {
      filters.add(
          CustomFilters.eqFilter(prefix + BaleenProperties.MENTION_IDS, entityFilter.getMentionId(), operatorMode));
    }

    if (entityFilter.getType() != null) {
      filters.add(CustomFilters.eqFilter(prefix + BaleenProperties.TYPE, entityFilter.getType(), operatorMode));
    }
    if (entityFilter.getSubType() != null) {
      filters.add(CustomFilters.eqFilter(prefix + BaleenProperties.SUBTYPE, entityFilter.getSubType(), operatorMode));
    }


    if (entityFilter.getProperties() != null) {
      for (final Map.Entry<String, Object> e : entityFilter.getProperties().entrySet()) {
        filters.add(
            CustomFilters.eqFilter(prefix + BaleenProperties.PROPERTIES + "." + e.getKey(), e.getValue(),
                operatorMode));
      }
    }

    if (entityFilter.getStartTimestamp() != null) {

      filters.add(
          Filters.gte(prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.START_TIMESTAMP,
              entityFilter.getStartTimestamp().getTime()));
    }


    if (entityFilter.getEndTimestamp() != null) {
      filters.add(
          Filters.gte(prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.STOP_TIMESTAMP,
              entityFilter.getEndTimestamp().getTime()));
    }

    if (entityFilter.getWithin() != null) {
      final GeoBox within = entityFilter.getWithin();

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
      filters
          .add(Filters.geoWithin(prefix + BaleenProperties.PROPERTIES + "." + BaleenProperties.GEOJSON, polygon));
    }

    return FilterUtils.combine(filters);
  }

  public static Optional<Bson> createFilter(final EntitySearch entitySearch) {
    return createFilter(Optional.ofNullable(entitySearch.getEntityFilter()), "", false);
  }

  public static Stream<Bson> createFilters(final Stream<EntityFilter> filters, final boolean operatorMode) {
    return filters
        .map(f -> createFilter(Optional.ofNullable(f), "", operatorMode))
        .filter(Optional::isPresent)
        .map(Optional::get);
  }
}
