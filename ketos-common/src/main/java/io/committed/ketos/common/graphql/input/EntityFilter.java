package io.committed.ketos.common.graphql.input;

import java.util.Date;

import lombok.Data;

import io.committed.invest.core.dto.analytic.GeoBox;
import io.committed.invest.core.dto.analytic.GeoRadius;
import io.committed.invest.core.dto.collections.PropertiesMap;

/** Search query for an entity. */
@Data
public class EntityFilter {

  private String id;
  private String docId;
  private String type;
  private String subType;
  private String value;
  private PropertiesMap properties;

  private String mentionId;

  private Date startTimestamp;
  private Date endTimestamp;

  private GeoBox within;

  private GeoRadius near;
}
