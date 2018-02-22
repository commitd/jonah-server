package io.committed.ketos.graphql.baleen.document;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.geojson.GeoJsonObject;
import org.geojson.LineString;
import org.geojson.LngLatAlt;
import org.geojson.MultiPoint;
import org.geojson.MultiPolygon;
import org.geojson.Point;
import org.geojson.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.davidmoten.geo.GeoHash;
import io.committed.invest.core.dto.analytic.GeoBox;
import io.committed.invest.core.dto.analytic.GeoLocation;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.constants.BaleenTypes;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.general.NamedGeoLocation;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.output.MentionSearch;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;


@GraphQLService
public class DocumentLocationField extends AbstractGraphQlService {

  // Use out own object mapper, rather than Spring's since we don't need any configuration
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Autowired
  public DocumentLocationField(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  // Extend corpus


  @GraphQLQuery(name = "locations", description = "Get locations in a document")
  public Flux<NamedGeoLocation> getByDocument(@GraphQLContext final BaleenDocument document,
      @GraphQLArgument(name = "left", description = "Left bound (west)") final Double left,
      @GraphQLArgument(name = "right", description = "Right bound (east)") final Double right,
      @GraphQLArgument(name = "top", description = "Top bound (north)") final Double top,
      @GraphQLArgument(name = "bottom", description = "Bottom bound (south)") final Double bottom,
      @GraphQLArgument(name = "offset", defaultValue = "0") final int offset,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {


    // Create a fake search
    final MentionFilter filter = new MentionFilter();
    filter.setDocId(document.getId());
    filter.setType(BaleenTypes.LOCATION);
    if (left != null && right != null && top != null && bottom != null) {
      filter.setWithin(new GeoBox(top, right, bottom, left));
    }
    final MentionSearch search = new MentionSearch(document, filter);

    return getProvidersFromContext(document, MentionProvider.class, hints)
        .flatMap(p -> p.search(search, offset, limit).getResults())
        // Require geo
        .filter(m -> {
          final Object geoJson = m.getProperties().get("geoJson");
          return geoJson != null && geoJson instanceof String;
        }).map(m -> {
          // TODO: Baleen should return produce more sensible here... but that's not in our gift to
          // change

          // bit crazy to have to do this work
          final String geoJson = (String) m.getProperties().get("geoJson");

          // TODO: Add location name to GeoLocation (being the mention value to this)

          final Optional<NamedGeoLocation> l = convertToPoint(geoJson);

          if (l.isPresent()) {
            l.get().setName(m.getValue());
          }

          return l;
        }).filter(Optional::isPresent).map(Optional::get)
        // TODO: Distinct on location... (for when we add name above... that shouldn't be part of
        // it... we should consolidate)
        .distinct().filter(Objects::nonNull);

  }


  private Optional<NamedGeoLocation> convertToPoint(final String geoJson) {
    GeoJsonObject object;
    try {
      object = MAPPER.readValue(geoJson, GeoJsonObject.class);
    } catch (final Exception e) {
      return Optional.empty();
    }


    if (object instanceof Point) {
      final Point p = (Point) object;
      return toGeoLocation(p.getCoordinates());
    } else if (object instanceof Polygon) {
      final Polygon p = (Polygon) object;
      return toGeoLocation(p.getExteriorRing());

    } else if (object instanceof MultiPolygon) {
      return toGeoLocation((MultiPolygon) object);
    } else if (object instanceof MultiPoint) {
      return toGeoLocation(((MultiPoint) object).getCoordinates());
    } else if (object instanceof LineString) {
      return toGeoLocation(((LineString) object).getCoordinates());
    } else {
      // TODO: Could implement others here but I think Baleen only outputs the above too..
    }

    return Optional.empty();
  }

  private Optional<NamedGeoLocation> toGeoLocation(final MultiPolygon mp) {

    final List<List<List<LngLatAlt>>> coordinates = mp.getCoordinates();
    // We'll just take the first... there's no good way here
    // if it was a 'set of affliated countries' it might be countries around the globe, in which
    // case averaging is silly

    // So we take the longest polygon. Which could be the smallest and more detailed (NOTE this is
    // no necessarily the biggest area)

    List<LngLatAlt> best = null;

    for (final List<List<LngLatAlt>> polygon : coordinates) {
      final List<LngLatAlt> exteriorRing = polygon.get(0);
      if (best == null || exteriorRing.size() > best.size()) {
        best = exteriorRing;
      }
    }

    return toGeoLocation(best);
  }

  private Optional<NamedGeoLocation> toGeoLocation(final List<LngLatAlt> coordinates) {
    // Simply average the positions

    if (coordinates == null || coordinates.isEmpty()) {
      return Optional.empty();
    } else {
      double lat = 0;
      double lon = 0;

      for (final LngLatAlt c : coordinates) {
        lat += c.getLatitude();
        lon += c.getLongitude();
      }
      final int len = coordinates.size();
      return Optional.of(new NamedGeoLocation(lat / len, lon / len));
    }
  }

  private Optional<NamedGeoLocation> toGeoLocation(final LngLatAlt coordinates) {
    return Optional.of(new NamedGeoLocation(coordinates.getLatitude(), coordinates.getLongitude()));
  }


  @GraphQLQuery(name = "geohash")
  public String getGeohash(@GraphQLContext final GeoLocation location,
      @GraphQLArgument(name = "precision", defaultValue = "7") final int precision) {
    final double lat = location.getLat();
    final double lon = location.getLon();

    if (Double.isFinite(lon) && Double.isFinite(lat)) {
      return GeoHash.encodeHash(lat, lon, precision);
    }

    return null;
  }

}

