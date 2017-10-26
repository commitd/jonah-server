package io.committed.ketos.plugins.data.baleenmongo.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.committed.ketos.plugins.data.baleen.BaleenDocument;
import io.committed.ketos.plugins.data.baleen.BaleenEntity;
import io.committed.ketos.plugins.data.baleenmongo.dao.MongoEntities;
import io.committed.ketos.plugins.data.baleenmongo.repository.BaleenEntitiesRepository;
import io.committed.ketos.plugins.graphql.baleenservices.providers.EntityProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MongoEntityProvider implements EntityProvider {

  @Autowired
  private BaleenEntitiesRepository entities;

  @Override
  public Mono<BaleenEntity> getById(final String id) {
    return entities.findById(id).map(MongoEntities::toEntity);
  }

  @Override
  public Flux<BaleenEntity> getByDocument(final BaleenDocument document) {
    return entities.findByDocId(document.getId()).map(MongoEntities::toEntity);
  }



  // @GraphQLQuery(name = "entitiesByDocument")
  // public Flux<BaleenEntity> getByDocumentId(@GraphQLArgument(name = "id") final String id) {
  // return entities.findByDocId(id).map(MongoEntities::toEntity);
  // }
  //
  //
  // @Override
  // @GraphQLQuery(name = "entities")
  // public Flux<BaleenEntity> getByDocument(@GraphQLContext final BaleenDocument document) {
  // return filterEntities(getByDocumentId(document.getId()), null, null, 0);
  // }
  //
  // @Override
  // @GraphQLQuery(name = "entities")
  // public Flux<BaleenEntity> getByDocumentAndType(
  // @GraphQLArgument(name = "document") @GraphQLContext final BaleenDocument document,
  // @GraphQLArgument(name = "type", description = "The type of the entity") final String type) {
  //
  // return filterEntities(getByDocumentId(document.getId()), type, null, 0);
  // }
  //
  // @Override
  // @GraphQLQuery(name = "entities")
  // public Flux<BaleenEntity> getByDocumentAndValue(
  // @GraphQLArgument(name = "document") @GraphQLContext final BaleenDocument document,
  // @GraphQLArgument(name = "value", description = "A value of the entity") final String value) {
  //
  // return filterEntities(getByDocumentId(document.getId()), null, value, 0);
  // }
  //
  // // TODO: This should work but bug in spqr - accepted
  // // @GraphQLQuery(name = "entities")
  // // public Flux<Entity> getByDocumentAndType(
  // // @GraphQLArgument(name = "document") @GraphQLContext Document document,
  // // @GraphQLArgument(name = "type", description = "The type of the entity",
  // // defaultValueProvider = NullProvider.class) String type,
  // // @GraphQLArgument(name = "value", description = "A value of the entity",
  // // defaultValueProvider = NullProvider.class) String value,
  // // @GraphQLArgument(name = "limit", defaultValue = "0") int limit) {
  // //
  // // return filterEntities(getByDocumentId(document.getId()), type, value, limit)
  // // .collect(Collectors.toList());
  // // }
  //
  //
  //
  // @Override
  // @GraphQLQuery(name = "entity")
  // public Mono<BaleenEntity> getById(@GraphQLArgument(name = "id") @GraphQLId final String id) {
  // return entities.findById(id).map(MongoEntities::toEntity);
  // }
  //
  // @Override
  // public Mono<BaleenEntity> mentionEntity(final BaleenMention mention) {
  // return getById(mention.getEntityId());
  // }

}
