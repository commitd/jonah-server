package io.committed.ketos.common.graphql.input;

import java.util.Date;
import java.util.Map;
import io.committed.invest.core.dto.analytic.GeoBox;
import lombok.Data;

@Data
public class MentionFilter {

  private String docId;

  private String id;
  private String entityId;

  private String type;
  private String value;

  private Map<String, Object> properties;

  // TODO: WE could have begin / end here but that might be too much detail...
  // private int begin;
  // private int end;

  // TODO: Add type specific ... timestamp, geobounds... this what will make it more useful than just
  // the query by example stuff as above

  private Date startTimestamp;
  private Date endTimestamp;

  private GeoBox within;

}
