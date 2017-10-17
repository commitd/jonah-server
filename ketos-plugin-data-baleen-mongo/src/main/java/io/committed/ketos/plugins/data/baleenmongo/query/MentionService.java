package io.committed.query;

import java.util.Optional;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.committed.dao.repository.BaleenEntitiesRepository;
import io.committed.dto.Document;
import io.committed.dto.Entity;
import io.committed.dto.Mention;
import io.committed.dto.Relation;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;

@Component
public class MentionService {

  @Autowired
  BaleenEntitiesRepository entities;


  private Stream<Entity> getByDocumentId(@GraphQLArgument(name = "id") String id) {
    return entities.findByDocId(id).stream().map(Entity::new);
  }

  private Stream<Mention> getMentionsByDocumentId(String documentId) {
    return getByDocumentId(documentId).flatMap(e -> e.getMentions().stream());
  }

  private Optional<Mention> relationMentionById(Relation relation, String sourceId) {
    return getMentionsByDocumentId(relation.getDocId()).filter(m -> sourceId.equals(m.getId()))
        .findFirst();
  }


  @GraphQLQuery(name = "mentions")
  public Stream<Mention> getMentionsByDocument(@GraphQLContext Document document) {
    return getMentionsByDocumentId(document.getId());
  }

  @GraphQLQuery(name = "source")
  public Optional<Mention> source(@GraphQLContext @NotNull Relation relation) {
    return relationMentionById(relation, relation.getSourceId());
  }

  @GraphQLQuery(name = "target")
  public Optional<Mention> target(@GraphQLContext @NotNull Relation relation) {
    return relationMentionById(relation, relation.getTargetId());
  }

}
