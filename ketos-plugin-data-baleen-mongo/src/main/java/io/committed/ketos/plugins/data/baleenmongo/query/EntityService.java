package io.committed.query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.committed.dao.repository.BaleenEntitiesRepository;
import io.committed.dto.Document;
import io.committed.dto.Entity;
import io.committed.dto.Mention;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;

@Component
public class EntityService {

  @Autowired
  BaleenEntitiesRepository entities;

  private Stream<Entity> filterEntities(Stream<Entity> stream, String type, String value,
      int limit) {
    if (type != null) {
      stream = stream.filter(e -> type.equals(e.getType().get()));
    }
    if (value != null) {
      stream = stream.filter(e -> e.getValues().contains(value));
    }
    if (limit > 0) {
      stream = stream.limit(limit);
    }
    return stream;
  }

  // FIXME: should be entities - current bug in spqr
  @GraphQLQuery(name = "allEntities")
  public List<Entity> getEntities(
      @GraphQLArgument(name = "type", description = "The type of the entity") String type,
      @GraphQLArgument(name = "value", description = "A value of the entity") String value,
      @GraphQLArgument(name = "limit", defaultValue = "0") int limit) {

    return filterEntities(
        StreamSupport.stream(entities.findAll().spliterator(), false).map(Entity::new), type, value,
        limit).collect(Collectors.toList());
  }



  @GraphQLQuery(name = "entitiesByDocument")
  public Stream<Entity> getByDocumentId(@GraphQLArgument(name = "id") String id) {
    return entities.findByDocId(id).stream().map(Entity::new);
  }


  @GraphQLQuery(name = "entities")
  public Stream<Entity> getByDocument(@GraphQLContext Document document) {
    return filterEntities(getByDocumentId(document.getId()), null, null, 0);
  }

  @GraphQLQuery(name = "entities")
  public Stream<Entity> getByDocumentAndType(
      @GraphQLArgument(name = "document") @GraphQLContext Document document,
      @GraphQLArgument(name = "type", description = "The type of the entity") String type) {

    return filterEntities(getByDocumentId(document.getId()), type, null, 0);
  }

  @GraphQLQuery(name = "entities")
  public Stream<Entity> getByDocumentAndValue(
      @GraphQLArgument(name = "document") @GraphQLContext Document document,
      @GraphQLArgument(name = "value", description = "A value of the entity") String value) {

    return filterEntities(getByDocumentId(document.getId()), null, value, 0);
  }

  // TODO: This should work but bug in spqr - accepted
  // @GraphQLQuery(name = "entities")
  // public List<Entity> getByDocumentAndType(
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
  public Optional<Entity> getById(@GraphQLArgument(name = "id") @GraphQLId String id) {
    return Optional.ofNullable(entities.findOne(id)).map(Entity::new);
  }

  @GraphQLQuery(name = "of")
  public Optional<Entity> mentionEntity(@GraphQLContext Mention mention) {
    return getById(mention.getEntityId());
  }

}