package io.committed.ketos.plugins.data.baleenmongo.dao;

import io.committed.ketos.plugins.data.baleen.BaleenMention;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MongoMention {

  private double confidence;
  private String externalId;
  private int begin;
  private int end;
  private String type;
  private String value;


  public BaleenMention toMention(final String entityId) {
    final BaleenMention m = new BaleenMention();
    m.setEntityId(entityId);
    m.setId(getExternalId());
    m.setConfidence(getConfidence());
    m.setBegin(getBegin());
    m.setEnd(getEnd());
    m.setType(getType());
    m.setValue(getValue());
    return m;
  }
}
