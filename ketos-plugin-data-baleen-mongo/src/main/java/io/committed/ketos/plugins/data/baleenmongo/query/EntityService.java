package io.committed.ketos.plugins.data.baleenmongo.query;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.plugins.data.baleenmongo.dao.BaleenEntities;
import io.committed.ketos.plugins.data.baleenmongo.repository.BaleenEntitiesRepository;
import io.committed.ketos.plugins.graphql.baleen.Document;
import io.committed.ketos.plugins.graphql.baleen.Entity;
import io.committed.ketos.plugins.graphql.baleen.Mention;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@VesselGraphQlService
public class EntityService {

  @Autowired
  private BaleenEntitiesRepository entities;

  private Flux<Entity> filterEntities(Flux<Entity> stream, final String type,
      final String value,
      final int limit) {
    if (type != null) {
      stream = stream.filter(e -> type.equals(e.getType().get()));
    }
    if (value != null) {
      stream = stream.filter(e -> e.getValues().contains(value));
    }
    if (limit > 0) {
      stream = stream.take(limit);
    }
    return stream;
  }

  // FIXME: should be entities - current bug in spqr
  @GraphQLQuery(name = "allEntities")
  public Flux<Entity> getEntities(
      @GraphQLArgument(name = "type", description = "The type of the entity") final String type,
      @GraphQLArgument(name = "value", description = "A value of the entity") final String value,
      @GraphQLArgument(name = "limit", defaultValue = "0") final int limit) {

    return filterEntities(
        entities.findAll().map(BaleenEntities::toEntity),
        type, value,
        limit);
  }



  @GraphQLQuery(name = "entitiesByDocument")
  public Flux<Entity> getByDocumentId(@GraphQLArgument(name = "id") final String id) {
    return entities.findByDocId(id).map(BaleenEntities::toEntity);
  }


  @GraphQLQuery(name = "entities")
  public Flux<Entity> getByDocument(@GraphQLContext final Document document) {
    return filterEntities(getByDocumentId(document.getId()), null, null, 0);
  }

  @GraphQLQuery(name = "entities")
  public Flux<Entity> getByDocumentAndType(
      @GraphQLArgument(name = "document") @GraphQLContext final Document document,
      @GraphQLArgument(name = "type", description = "The type of the entity") final String type) {

    return filterEntities(getByDocumentId(document.getId()), type, null, 0);
  }

  @GraphQLQuery(name = "entities")
  public Flux<Entity> getByDocumentAndValue(
      @GraphQLArgument(name = "document") @GraphQLContext final Document document,
      @GraphQLArgument(name = "value", description = "A value of the entity") final String value) {

    return filterEntities(getByDocumentId(document.getId()), null, value, 0);
  }

  // TODO: This should work but bug in spqr - accepted
  // @GraphQLQuery(name = "entities")
  // public Flux<Entity> getByDocumentAndType(
  // @GraphQLArgument(name = "document") @GraphQLContext Document document,
  // @GraphQLArgument(name = "type", description = "The type of the entity",
  // defaultValueProvider = NullProvider.class) String type,
  // @GraphQLArgument(name = "value", description = "A value of the entity",
  // defaultValueProvider = NullProvider.class) String value,
  // @GraphQLArgument(name = "limit", defaultValue = "0") int limit) {
  //
  // return filterEntities(getByDocumentId(document.getId()), type, value, limit)
  // .collect(Collectors.toList());
  // }



  @GraphQLQuery(name = "entity")
  public Mono<Entity> getById(@GraphQLArgument(name = "id") @GraphQLId final String id) {
    return entities.findById(id).map(BaleenEntities::toEntity);
  }

  @GraphQLQuery(name = "of")
  public Mono<Entity> mentionEntity(@GraphQLContext final Mention mention) {
    return getById(mention.getEntityId());
  }

}
