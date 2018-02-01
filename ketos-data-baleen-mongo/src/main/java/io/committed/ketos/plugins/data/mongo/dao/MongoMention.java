package io.committed.ketos.plugins.data.mongo.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import com.google.common.collect.Sets;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.graphql.input.MentionProbe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MongoMention {

  private static final Set<String> NON_PROPERTIES = Collections.unmodifiableSet(
      Sets.newHashSet("confidence", "externalId", "begin", "end", "type", "value", "geoJson"));

  private double confidence;
  private String externalId;
  private int begin;
  private int end;
  private String type;
  private String value;

  // Though not part of the Mongo schema from Baleen some aggregations create this
  private String entityId;

  private Map<String, Object> properties = new HashMap<>();


  public MongoMention(final Document m) {

    confidence = m.getDouble("confidence");
    externalId = m.getString("externalId");
    begin = m.getInteger("begin", 0);
    end = m.getInteger("end", 0);
    type = m.getString("type");
    value = m.get("value").toString();

    // Carried through by some aggregations, could be string / objectid / etc
    final Object entity = m.get("entityId");
    if (entity != null) {
      entityId = entity.toString();
    }

    // Baleen's output of GeoJson a hack really, without it begin a string it'll be deserialsed as a
    // bson.Document.
    final Object geoJson = m.get("geoJson");
    if (geoJson != null) {
      if (geoJson instanceof String) {
        properties.put("geoJson", geoJson);
      } else if (geoJson instanceof Document) {
        properties.put("geoJson", ((Document) geoJson).toJson());
      }

    }

    readProperties(m);

  }

  private void readProperties(final Document m) {
    m.entrySet().stream().filter(e -> !NON_PROPERTIES.contains(e.getKey()))
        .forEach(e -> properties.put(e.getKey(), e.getValue()));
  }

  public BaleenMention toMention(final String entityId) {
    return BaleenMention.builder()
        .entityId(entityId)
        .id(getExternalId())
        .confidence(getConfidence())
        .begin(getBegin())
        .end(getEnd())
        .type(getType())
        .value(getValue())
        .properties(getProperties())
        .build();
  }

  public BaleenMention toMention() {
    // This will only work for aggregation outputs... which ave the entityId specifically added to them
    // deserialising from the entities collection does not have entityId per mention.
    return toMention(getEntityId());
  }

  public static Document fromProbe(final MentionProbe probe) {
    final Document document = new Document();

    document.put("begin", probe.getBegin());
    document.put("confidence", probe.getConfidence());
    document.put("end", probe.getEnd());
    document.put("externalId", probe.getId());
    if (probe.getProperties() != null) {
      probe.getProperties()
          .entrySet()
          .forEach(e -> document.put(e.getKey(), e.getValue()));
    }
    document.put("type", probe.getType());
    document.put("value", probe.getValue());

    // This doesn't exist within the mongo schema... but it does exist with the aggregation to create
    // mentiosn we need is extractMentions in MongoMentionProvider
    document.put("entityId", probe.getEntityId());
    document.put("docId", probe.getDocId());

    return document;
  }

}
