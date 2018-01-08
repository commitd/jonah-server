package io.committed.ketos.plugins.data.mongo.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import com.google.common.collect.Sets;
import io.committed.ketos.common.data.BaleenMention;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MongoMention {

  private static final Set<String> NON_PROPERTIES = Collections.unmodifiableSet(
      Sets.newHashSet("confidence", "externalId", "begin", "end", "type", "value"));

  private double confidence;
  private String externalId;
  private int begin;
  private int end;
  private String type;
  private String value;

  private Map<String, Object> properties = new HashMap<>();


  public MongoMention(final Document m) {

    confidence = m.getDouble("confidence");
    externalId = m.getString("externalId");
    begin = m.getInteger("begin", 0);
    end = m.getInteger("end", 0);
    type = m.getString("type");
    value = m.get("value").toString();

    readProperties(m);

  }

  private void readProperties(final Document m) {
    m.entrySet().stream().filter(e -> !NON_PROPERTIES.contains(e.getKey()))
        .forEach(e -> properties.put(e.getKey(), e.getValue()));
  }

  public BaleenMention toMention(final String entityId) {
    final BaleenMention m = new BaleenMention();
    m.setEntityId(entityId);
    m.setId(getExternalId());
    m.setConfidence(getConfidence());
    m.setBegin(getBegin());
    m.setEnd(getEnd());
    m.setType(getType());
    m.setValue(getValue());
    m.setProperties(getProperties());
    return m;
  }



}
