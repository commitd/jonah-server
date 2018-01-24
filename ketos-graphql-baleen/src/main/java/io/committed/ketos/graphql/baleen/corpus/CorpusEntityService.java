package io.committed.ketos.graphql.baleen.corpus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TermCount;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@GraphQLService
public class CorpusEntityService extends AbstractGraphQlService {

  @Autowired
  public CorpusEntityService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }


  @GraphQLQuery(name = "entities", description = "Get all entities")
  public Flux<BaleenEntity> getByDocument(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "type", description = "The type of the entity") final String type,
      @GraphQLArgument(name = "value", description = "A value of the entity") final String value,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    if (!StringUtils.isEmpty(type) && !StringUtils.isEmpty(value)) {
      return getProviders(corpus, EntityProvider.class, hints)
          .flatMap(p -> p.getByTypeAndValue(type, value, limit))
          .doOnNext(eachAddParent(corpus));
    } else if (!StringUtils.isEmpty(value)) {
      return getProviders(corpus, EntityProvider.class, hints)
          .flatMap(p -> p.getByValue(value, limit))
          .doOnNext(eachAddParent(corpus));
    } else if (!StringUtils.isEmpty(type)) {
      return getProviders(corpus, EntityProvider.class, hints)
          .flatMap(p -> p.getByType(type, limit))
          .doOnNext(eachAddParent(corpus));
    } else {
      // Both are null
      return getProviders(corpus, EntityProvider.class, hints)
          .flatMap(p -> p.getAll(0, limit))
          .doOnNext(eachAddParent(corpus));
    }

  }

  @GraphQLQuery(name = "entity", description = "Get entities by id")
  public Mono<BaleenEntity> getById(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "id") @GraphQLId final String id, @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, EntityProvider.class, hints)
        .flatMap(p -> p.getById(id))
        .next()
        .doOnNext(eachAddParent(corpus));
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

