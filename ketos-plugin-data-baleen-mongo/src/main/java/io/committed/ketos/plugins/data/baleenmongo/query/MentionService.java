package io.committed.ketos.plugins.data.baleenmongo.query;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.plugins.data.baleenmongo.dao.BaleenEntities;
import io.committed.ketos.plugins.data.baleenmongo.dto.Document;
import io.committed.ketos.plugins.data.baleenmongo.dto.Entity;
import io.committed.ketos.plugins.data.baleenmongo.dto.Mention;
import io.committed.ketos.plugins.data.baleenmongo.dto.Relation;
import io.committed.ketos.plugins.data.baleenmongo.repository.BaleenEntitiesRepository;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;


@VesselGraphQlService
public class MentionService {

  @Autowired
  BaleenEntitiesRepository entities;


  private Stream<Entity> getByDocumentId(@GraphQLArgument(name = "id") final String id) {
    return entities.findByDocId(id).stream().map(BaleenEntities::toEntity);
  }

  private Stream<Mention> getMentionsByDocumentId(final String documentId) {
    return getByDocumentId(documentId).flatMap(e -> e.getMentions().stream());
  }

  private Optional<Mention> relationMentionById(final Relation relation, final String sourceId) {
    return getMentionsByDocumentId(relation.getDocId()).filter(m -> sourceId.equals(m.getId()))
        .findFirst();
  }


  @GraphQLQuery(name = "mentions")
  public Stream<Mention> getMentionsByDocument(@GraphQLContext final Document document) {
    return getMentionsByDocumentId(document.getId());
  }

  @GraphQLQuery(name = "source")
  public Optional<Mention> source(@GraphQLContext final Relation relation) {
    return relationMentionById(relation, relation.getSourceId());
  }

  @GraphQLQuery(name = "target")
  public Optional<Mention> target(@GraphQLContext final Relation relation) {
    return relationMentionById(relation, relation.getTargetId());
  }

}
