package io.committed.ketos.plugins.data.baleenmongo.dao;

import io.committed.ketos.plugins.data.baleenmongo.dto.Mention;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaleenMention {

  private double confidence;
  private String externalId;
  private int begin;
  private int end;
  private String type;
  private String value;


  public Mention toMention(final String entityId) {
    final Mention m = new Mention();
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
