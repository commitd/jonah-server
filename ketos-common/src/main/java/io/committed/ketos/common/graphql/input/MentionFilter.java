package io.committed.ketos.common.graphql.input;

import java.util.Date;
import io.committed.invest.core.dto.analytic.GeoBox;
import io.committed.invest.core.dto.collections.PropertiesMap;
import lombok.Data;

@Data
public class MentionFilter {

  private String docId;

  private String id;
  private String entityId;

  private String type;
  private String subType;
  private String value;

  private PropertiesMap properties;

  // TODO: WE could have begin / end as startAfter/startBefore
  // private int begin;
  // private int end;

  private Date startTimestamp;
  private Date endTimestamp;

  private GeoBox within;

}
