package io.committed.ketos.plugins.data.baleenmongo.dao;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

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

}
