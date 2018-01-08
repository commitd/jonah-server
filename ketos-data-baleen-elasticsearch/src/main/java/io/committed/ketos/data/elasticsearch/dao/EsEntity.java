package io.committed.ketos.data.elasticsearch.dao;

import java.util.Collections;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EsEntity {

  private String externalId;

  private double confidence;

  private int begin;

  private int end;

  private String type;

  private String value;

  private Date timestampStart;

  private Date timestampStop;

  // TOOO: Verify that string works ok here
  private String geoJson;

  // Used to carry through flux, etc
  private transient String documentId;

  public BaleenEntity toBaleenEntity() {
    return BaleenEntity.builder().docId(documentId).id(externalId)
        .mentions(Collections.singletonList(toBaleenMention())).build();
  }

  public BaleenMention toBaleenMention() {
    return BaleenMention.builder().begin(begin).end(end).confidence(confidence).entityId(externalId)
        .id(externalId).type(type).value(value).build();
  }

}
