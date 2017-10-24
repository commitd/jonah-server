package io.committed.ketos.plugins.data.baleenmongo.query;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.plugins.data.baleenmongo.dao.MongoEntities;
import io.committed.ketos.plugins.data.baleenmongo.repository.BaleenEntitiesRepository;
import io.committed.ketos.plugins.graphql.baleen.Document;
import io.committed.ketos.plugins.graphql.baleen.Entity;
import io.committed.ketos.plugins.graphql.baleen.Mention;
import io.committed.ketos.plugins.graphql.baleen.Relation;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@VesselGraphQlService
public class MentionService {

  @Autowired
  BaleenEntitiesRepository entities;


  private Flux<Entity> getByDocumentId(@GraphQLArgument(name = "id") final String id) {
    return entities.findByDocId(id).map(MongoEntities::toEntity);
  }

  private Flux<Mention> getMentionsByDocumentId(final String documentId) {
    return getByDocumentId(documentId).flatMap(e -> Flux.fromIterable(e.getMentions()));
  }

  private Mono<Mention> relationMentionById(final Relation relation, final String sourceId) {
    return getMentionsByDocumentId(relation.getDocId()).filter(m -> sourceId.equals(m.getId()))
        .next();
  }


  @GraphQLQuery(name = "mentions")
  public Flux<Mention> getMentionsByDocument(@GraphQLContext final Document document) {
    return getMentionsByDocumentId(document.getId());
  }

  @GraphQLQuery(name = "source")
  public Mono<Mention> source(@GraphQLContext final Relation relation) {
    return relationMentionById(relation, relation.getSourceId());
  }

  @GraphQLQuery(name = "target")
  public Mono<Mention> target(@GraphQLContext final Relation relation) {
    return relationMentionById(relation, relation.getTargetId());
  }

}
