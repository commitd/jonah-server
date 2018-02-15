package io.committed.ketos.common.graphql.input;

import java.util.Date;
import java.util.Map;
import io.committed.invest.core.dto.analytic.GeoBox;
import lombok.Data;

@Data
public class EntityFilter {

  private String id;
  private String docId;
  private String type;
  private String subType;
  private String value;
  private Map<String, Object> properties;

  private String mentionId;

  private Date startTimestamp;
  private Date endTimestamp;

  private GeoBox within;
}
