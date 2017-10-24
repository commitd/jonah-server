package io.committed.ketos.plugins.data.baleenmongo.query;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.plugins.data.baleenmongo.dao.MongoEntities;
import io.committed.ketos.plugins.data.baleenmongo.repository.BaleenEntitiesRepository;
import io.committed.ketos.plugins.graphql.baleen.BaleenDocument;
import io.committed.ketos.plugins.graphql.baleen.BaleenEntity;
import io.committed.ketos.plugins.graphql.baleen.BaleenMention;
import io.committed.ketos.plugins.graphql.baleen.BaleenRelation;
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


  private Flux<BaleenEntity> getByDocumentId(@GraphQLArgument(name = "id") final String id) {
    return entities.findByDocId(id).map(MongoEntities::toEntity);
  }

  private Flux<BaleenMention> getMentionsByDocumentId(final String documentId) {
    return getByDocumentId(documentId).flatMap(e -> Flux.fromIterable(e.getMentions()));
  }

  private Mono<BaleenMention> relationMentionById(final BaleenRelation relation, final String sourceId) {
    return getMentionsByDocumentId(relation.getDocId()).filter(m -> sourceId.equals(m.getId()))
        .next();
  }


  @GraphQLQuery(name = "mentions")
  public Flux<BaleenMention> getMentionsByDocument(@GraphQLContext final BaleenDocument document) {
    return getMentionsByDocumentId(document.getId());
  }

  @GraphQLQuery(name = "source")
  public Mono<BaleenMention> source(@GraphQLContext final BaleenRelation relation) {
    return relationMentionById(relation, relation.getSourceId());
  }

  @GraphQLQuery(name = "target")
  public Mono<BaleenMention> target(@GraphQLContext final BaleenRelation relation) {
    return relationMentionById(relation, relation.getTargetId());
  }

}
