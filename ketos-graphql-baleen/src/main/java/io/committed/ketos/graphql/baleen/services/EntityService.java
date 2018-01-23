package io.committed.ketos.graphql.baleen.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TermCount;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.invest.server.data.services.DatasetProviders;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@GraphQLService
public class EntityService extends AbstractGraphQlService {

  @Autowired
  public EntityService(final DatasetProviders corpusProviders) {
    super(corpusProviders);
  }

  // Extend document

  @GraphQLQuery(name = "entities", description = "Get all entities")
  public Flux<BaleenEntity> getByDocument(@GraphQLContext final BaleenDocument document,
      @GraphQLArgument(name = "type", description = "The type of the entity") final String type,
      @GraphQLArgument(name = "value", description = "A value of the entity") final String value,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    if (type != null && value != null) {
      return getProvidersFromContext(document, EntityProvider.class, hints)
          .flatMap(p -> p.getByDocumentAndType(document, type, value, limit))
          .map(addContext(document)).doOnNext(BaleenEntity::addContextToMentions);
    } else if (value != null) {
      return getProvidersFromContext(document, EntityProvider.class, hints)
          .flatMap(p -> p.getByDocumentAndValue(document, value, limit)).map(addContext(document))
          .doOnNext(BaleenEntity::addContextToMentions);
    } else if (type != null) {
      return getProvidersFromContext(document, EntityProvider.class, hints)
          .flatMap(p -> p.getByDocumentAndType(document, type, limit)).map(addContext(document))
          .doOnNext(BaleenEntity::addContextToMentions);
    } else {
      // Both are null
      return getProvidersFromContext(document, EntityProvider.class, hints)
          .flatMap(p -> p.getByDocument(document).take(limit)).map(addContext(document))
          .doOnNext(BaleenEntity::addContextToMentions);
    }

  }



  // Extend corpus


  @GraphQLQuery(name = "entities", description = "Get all entities")
  public Flux<BaleenEntity> getByDocument(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "type", description = "The type of the entity") final String type,
      @GraphQLArgument(name = "value", description = "A value of the entity") final String value,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    if (!StringUtils.isEmpty(type) && !StringUtils.isEmpty(type)) {
      return getProviders(corpus, EntityProvider.class, hints)
          .flatMap(p -> p.getByTypeAndValue(type, value, limit)).map(addContext(corpus))
          .doOnNext(BaleenEntity::addContextToMentions);
    } else if (!StringUtils.isEmpty(value)) {
      return getProviders(corpus, EntityProvider.class, hints)
          .flatMap(p -> p.getByValue(value, limit)).map(addContext(corpus))
          .doOnNext(BaleenEntity::addContextToMentions);
    } else if (!StringUtils.isEmpty(type)) {
      return getProviders(corpus, EntityProvider.class, hints)
          .flatMap(p -> p.getByType(type, limit)).map(addContext(corpus))
          .doOnNext(BaleenEntity::addContextToMentions);
    } else {
      // Both are null
      return getProviders(corpus, EntityProvider.class, hints).flatMap(p -> p.getAll(0, limit))
          .map(addContext(corpus)).doOnNext(BaleenEntity::addContextToMentions);
    }

  }

  @GraphQLQuery(name = "entity", description = "Get entities by id")
  public Mono<BaleenEntity> getById(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "id") @GraphQLId final String id, @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, EntityProvider.class, hints).flatMap(p -> p.getById(id))
        .map(addContext(corpus)).next().doOnNext(BaleenEntity::addContextToMentions);
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

  // Extend mentions

  @GraphQLQuery(name = "entity", description = "Get entities for a mention")
  public Mono<BaleenEntity> mentionEntity(@GraphQLContext final BaleenMention mention,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProvidersFromContext(mention, EntityProvider.class, hints)
        .flatMap(p -> p.mentionEntity(mention)).map(addContext(mention)).next()
        .doOnNext(BaleenEntity::addContextToMentions);
  }


}

