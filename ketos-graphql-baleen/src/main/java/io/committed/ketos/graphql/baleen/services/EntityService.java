package io.committed.ketos.graphql.baleen.services;

import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.annotations.GraphQLService;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TermCount;
import io.committed.invest.server.data.query.DataHints;
import io.committed.invest.server.data.services.DatasetProviders;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@GraphQLService
public class EntityService extends AbstractGraphQlService {

  @Autowired
  public EntityService(final DatasetProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "allEntities", description = "Get all entities")
  public Flux<BaleenEntity> getByDocument(@GraphQLContext final BaleenDocument document,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProvidersFromContext(document, EntityProvider.class, hints)
        .flatMap(p -> p.getByDocument(document)).map(addContext(document));
  }

  @GraphQLQuery(name = "entities", description = "Get entities by type")
  public Flux<BaleenEntity> getByDocumentAndType(
      @GraphQLArgument(name = "document") @GraphQLContext final BaleenDocument document,
      @GraphQLNonNull @GraphQLArgument(name = "type",
          description = "The type of the entity") final String type,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return getProvidersFromContext(document, EntityProvider.class, hints)
        .flatMap(p -> p.getByDocumentAndType(document, type, limit)).map(addContext(document));
  }

  @GraphQLQuery(name = "entities", description = "Get entities by type and value")
  public Flux<BaleenEntity> getByDocumentAndValue(
      @GraphQLArgument(name = "document") @GraphQLContext final BaleenDocument document,
      @GraphQLNonNull @GraphQLArgument(name = "value",
          description = "A value of the entity") final String value,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return getProvidersFromContext(document, EntityProvider.class, hints)
        .flatMap(p -> p.getByDocumentAndValue(document, value, limit)).map(addContext(document));
  }

  @GraphQLQuery(name = "entities", description = "Get entities by type")
  public Flux<BaleenEntity> getByDocumentAndType(
      @GraphQLArgument(name = "document") @GraphQLContext final BaleenDocument document,
      @GraphQLArgument(name = "type", description = "The type of the entity") final String type,
      @GraphQLArgument(name = "value", description = "A value of the entity") final String value,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return getProvidersFromContext(document, EntityProvider.class, hints)
        .flatMap(p -> p.getByDocumentAndType(document, type, value, limit))
        .map(addContext(document));
  }



  @GraphQLQuery(name = "entity", description = "Get entities by id")
  public Mono<BaleenEntity> getById(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "id") @GraphQLId final String id, @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, EntityProvider.class, hints).flatMap(p -> p.getById(id))
        .map(addContext(corpus)).next();
  }


  @GraphQLQuery(name = "of", description = "Get entities for a mention")
  public Mono<BaleenEntity> mentionEntity(@GraphQLContext final BaleenMention mention,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProvidersFromContext(mention, EntityProvider.class, hints)
        .flatMap(p -> p.mentionEntity(mention)).map(addContext(mention)).next();
  }

  @GraphQLQuery(name = "entityCount", description = "Number of entities in corpus")
  public Mono<Long> getDocuments(@GraphQLContext final BaleenCorpus corpus, @GraphQLArgument(
      name = "hints",
      description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, EntityProvider.class, hints).flatMap(EntityProvider::count)
        .reduce(0L, (a, b) -> a + b);
  }

  @GraphQLQuery(name = "entityTypes", description = "Count of entities by entity type")
  public Mono<TermCount> getEntityTypes(@GraphQLContext final BaleenCorpus corpus, @GraphQLArgument(
      name = "hints",
      description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, EntityProvider.class, hints).flatMap(EntityProvider::countByType)
        .groupBy(TermBin::getTerm)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TermBin(g.key(), l)))
        .collectList().map(TermCount::new);
  }
}
