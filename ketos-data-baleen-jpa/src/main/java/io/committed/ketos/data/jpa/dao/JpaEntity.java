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
import reactor.core.publisher.Flux;

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

  // TODO: There is a geojson entry in the table.


  public BaleenEntity toBaleenEntity() {
    // Do the best we can to reconstruct the mentions...
    final List<BaleenMention> mentions = new ArrayList<>();
    for (int i = 0; i < Math.min(externalId.size(), value.size()); i++) {
      mentions.add(BaleenMention.builder().type(type).entityId(externalId.get(i))
          .value(value.get(i)).build());
    }

    return BaleenEntity.builder()
        .docId(docId)
        .mentions(Flux.fromStream(mentions.stream()))
        .build();
  }

  public JpaEntity(final EntityProbe probe) {
    // TODO: We do what we can here.. but in reality it's impossible to really do antyhing on these
    // fields
    // You'd need to rewrite the JPA output of Baleen for this to work well.
    // I suspect that even then the SPring JPA won't know what to do with the colleciton matching?
    // WE could also pull the data from the mention filter in (eg that value) but that's not

    this.docId = probe.getDocId();
    this.externalId = probe.getId() != null ? Collections.singletonList(probe.getId()) : null;
  }
}
