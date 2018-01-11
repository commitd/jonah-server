package io.committed.ketos.plugins.data.mongo.dao;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.committed.ketos.common.data.BaleenMention;
import lombok.Data;

@Document(collection = "entities")
@Data
public class FakeMongoEntities {

  @Id
  private String id;

  @JsonProperty("docId")
  private String docId;

  @JsonProperty("entities")
  private List<BaleenMention> entities;

}
