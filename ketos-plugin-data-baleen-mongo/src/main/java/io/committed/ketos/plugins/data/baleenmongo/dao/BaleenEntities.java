package io.committed.ketos.plugins.data.baleenmongo.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.committed.ketos.plugins.graphql.baleen.Entity;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Data;

@Document(collection = "entities")
@Data
public class BaleenEntities {

  @Id
  private String id;

  @JsonProperty("docId")
  private String docId;

  @JsonProperty("entities")
  @GraphQLQuery(name = "mentions", description = "The mentions of this entity")
  private List<BaleenMention> entities;

  public Entity toEntity() {
    final Entity entity = new Entity();
    entity.setId(getId());
    entity.setDocId(getDocId());
    entity.setMentions(getEntities().stream()
        .map(m -> m.toMention(getId()))
        .collect(Collectors.toList()));
    return entity;
  }

}
