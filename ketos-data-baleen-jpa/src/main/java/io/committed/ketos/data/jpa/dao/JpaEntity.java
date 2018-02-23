package io.committed.ketos.data.jpa.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.graphql.input.EntityProbe;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class JpaEntity {
  @Id
  private Long key;

  @Column(name = "doc_key")
  private String docId;

  private List<String> externalId;
  private String type;
  private List<String> value;

  // NOTE: There is a geojson entry in the table so that could be added


  public BaleenEntity toBaleenEntity() {
    // Do the best we can to reconstruct the mentions...
    final List<BaleenMention> mentions = new ArrayList<>();
    for (int i = 0; i < Math.min(externalId.size(), value.size()); i++) {
      mentions.add(BaleenMention.builder().type(type).entityId(externalId.get(i))
          .docId(docId).value(value.get(i)).build());
    }

    return BaleenEntity.builder()
        .docId(docId)
        .id(Long.toString(key))
        .type(type)
        .value(value.isEmpty() ? null : value.get(0))
        .build();
  }

  public JpaEntity(final EntityProbe probe) {
    this.docId = probe.getDocId();
    this.externalId = probe.getId() != null ? Collections.singletonList(probe.getId()) : null;
  }
}
