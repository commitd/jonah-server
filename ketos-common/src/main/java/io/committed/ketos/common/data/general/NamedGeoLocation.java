package io.committed.ketos.common.data.general;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import io.committed.invest.core.dto.analytic.GeoLocation;

/**
 * A geo location with a name.
 *
 * <p>Useful for converting say a location entity to a lat,lon marker
 */
@Data
@AllArgsConstructor()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NamedGeoLocation extends GeoLocation {

  private String name;

  private String type;

  public NamedGeoLocation(final double lat, final double lon) {
    this("", "", lat, lon);
  }

  public NamedGeoLocation(
      final String name, final String type, final double lat, final double lon) {
    super(lat, lon);
    this.name = name;
    this.type = type;
  }

  // Redeclare this so that they are noticed by GraphQl.

  @Override
  public double getLat() {
    return super.getLat();
  }

  @Override
  public double getLon() {
    return super.getLon();
  }
}
