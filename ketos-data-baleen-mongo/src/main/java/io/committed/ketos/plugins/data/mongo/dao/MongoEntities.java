package io.committed.ketos.plugins.data.mongo.dao;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.committed.ketos.common.data.BaleenEntity;
import lombok.Data;

@Document(collection = "entities")
@Data
public class MongoEntities {

  @Id
  private String id;

  @JsonProperty("docId")
  private String docId;

  @JsonProperty("entities")
  private List<MongoMention> entities;

  public BaleenEntity toEntity() {
    final BaleenEntity entity = new BaleenEntity();
    entity.setId(getId());
    entity.setDocId(getDocId());
    entity.setMentions(
        getEntities().stream().map(m -> m.toMention(getId())).collect(Collectors.toList()));
    return entity;
  }

}
