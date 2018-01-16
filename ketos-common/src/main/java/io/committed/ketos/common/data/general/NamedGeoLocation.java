package io.committed.ketos.common.data.general;

import io.committed.invest.core.dto.analytic.GeoLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NamedGeoLocation extends GeoLocation {

  private String name;

  private String type;

  public NamedGeoLocation(final double lat, final double lon) {
    super(lat, lon);
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
