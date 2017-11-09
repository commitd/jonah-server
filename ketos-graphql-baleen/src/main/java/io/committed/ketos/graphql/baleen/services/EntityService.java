package io.committed.ketos.graphql.baleen.services;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.vessel.core.dto.analytic.TermBin;
import io.committed.vessel.core.dto.analytic.TermCount;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.committed.vessel.server.data.services.DatasetProviders;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@VesselGraphQlService
public class EntityService extends AbstractGraphQlService {

  @Autowired
  public EntityService(final DatasetProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "allEntities")
  public Flux<BaleenEntity> getByDocument(@GraphQLContext final BaleenDocument document) {
    return getProvidersFromContext(document, EntityProvider.class)
        .flatMap(p -> p.getByDocument(document))
        .map(addContext(document));
  }

  @GraphQLQuery(name = "entities")
  public Flux<BaleenEntity> getByDocumentAndType(
      @GraphQLArgument(name = "document") @GraphQLContext final BaleenDocument document,
      @GraphQLNonNull @GraphQLArgument(name = "type",
          description = "The type of the entity") final String type,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit) {

    return getProvidersFromContext(document, EntityProvider.class)
        .flatMap(p -> p.getByDocumentAndType(document, type, limit))
        .map(addContext(document));
  }

  @GraphQLQuery(name = "entities")
  public Flux<BaleenEntity> getByDocumentAndValue(
      @GraphQLArgument(name = "document") @GraphQLContext final BaleenDocument document,
      @GraphQLNonNull @GraphQLArgument(name = "value",
          description = "A value of the entity") final String value,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit) {

    return getProvidersFromContext(document, EntityProvider.class)
        .flatMap(p -> p.getByDocumentAndValue(document, value, limit))
        .map(addContext(document));
  }

  @GraphQLQuery(name = "entities")
  public Flux<BaleenEntity> getByDocumentAndType(
      @GraphQLArgument(name = "document") @GraphQLContext final BaleenDocument document,
      @GraphQLArgument(name = "type", description = "The type of the entity") final String type,
      @GraphQLArgument(name = "value", description = "A value of the entity") final String value,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit) {

    return getProvidersFromContext(document, EntityProvider.class)
        .flatMap(p -> p.getByDocumentAndType(document, type, value, limit))
        .map(addContext(document));
  }



  @GraphQLQuery(name = "entity")
  public Mono<BaleenEntity> getById(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "id") @GraphQLId final String id) {
    return getProviders(corpus, EntityProvider.class)
        .flatMap(p -> p.getById(id))
        .map(addContext(corpus))
        .next();
  }


  @GraphQLQuery(name = "of")
  public Mono<BaleenEntity> mentionEntity(@GraphQLContext final BaleenMention mention) {
    return getProvidersFromContext(mention, EntityProvider.class)
        .flatMap(p -> p.mentionEntity(mention))
        .map(addContext(mention))
        .next();
  }

  @GraphQLQuery(name = "entityCount")
  public Mono<Long> getDocuments(@GraphQLContext final BaleenCorpus corpus) {
    return getProviders(corpus, EntityProvider.class)
        .flatMap(EntityProvider::count)
        .reduce(0L, (a, b) -> a + b);
  }

  @GraphQLQuery(name = "entityTypes")
  public Mono<TermCount> getEntityTypes(@GraphQLContext final BaleenCorpus corpus) {
    return getProviders(corpus, EntityProvider.class)
        .flatMap(EntityProvider::countByType)
        .groupBy(TermBin::getTerm)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TermBin(g.key(), l)))
        .collectList()
        .map(TermCount::new);


  }
}
