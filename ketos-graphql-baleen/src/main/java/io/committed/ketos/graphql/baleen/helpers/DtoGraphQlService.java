package io.committed.ketos.graphql.baleen.helpers;

import com.github.davidmoten.geo.GeoHash;
import io.committed.invest.core.dto.analytic.GeoLocation;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;

@GraphQLService
public class DtoGraphQlService {

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
