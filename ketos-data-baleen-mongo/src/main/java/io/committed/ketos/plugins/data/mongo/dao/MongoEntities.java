package io.committed.ketos.plugins.data.mongo.dao;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.input.EntityProbe;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@Document(collection = "entities")
@Data
@NoArgsConstructor
public class MongoEntities {

  @Id
  private String id;

  @JsonProperty("docId")
  private String docId;

  @JsonProperty("entities")
  private List<org.bson.Document> entities;

  public BaleenEntity toEntity() {
    return BaleenEntity.builder()
        .id(getId())
        .docId(getDocId())
        .mentions(Flux.fromStream(getEntities().stream()).map(d -> new MongoMention(d).toMention(getId())))
        .build();
  }

  public MongoEntities(final EntityProbe probe) {
    this.id = probe.getId();
    this.docId = probe.getDocId();
  }

}
