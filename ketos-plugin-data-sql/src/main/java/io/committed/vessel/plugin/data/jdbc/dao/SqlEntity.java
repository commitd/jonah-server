package io.committed.vessel.plugin.data.jdbc.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import io.committed.ketos.plugins.data.baleen.BaleenEntity;
import io.committed.ketos.plugins.data.baleen.BaleenMention;
import lombok.Data;

// TODO: Also geojson entity table.
@Entity
@Data
public class SqlEntity {
  @Id
  private Long key;

  @Column(name = "doc_key")
  private String docId;

  private List<String> externalId;
  private String type;
  private List<String> value;

  public BaleenEntity toBaleenEntity() {
    // Do the best we can to reconstruct the mentions...
    final List<BaleenMention> mentions = new ArrayList<>();
    for (int i = 0; i < Math.min(externalId.size(), value.size()); i++) {
      mentions.add(BaleenMention.builder()
          .type(type)
          .entityId(externalId.get(i))
          .value(value.get(i))
          .build());
    }

    return BaleenEntity.builder()
        .docId(docId)
        .mentions(mentions)
        .build();
  }
}
