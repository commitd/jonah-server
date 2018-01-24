package io.committed.ketos.graphql.baleen.corpus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@GraphQLService
public class RelationService extends AbstractGraphQlService {

  @Autowired
  public RelationService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }



  @GraphQLQuery(name = "relation", description = "Find a relation by id")
  public Mono<BaleenRelation> getById(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLNonNull @GraphQLArgument(name = "id") final String id,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, RelationProvider.class, hints)
        .flatMap(p -> p.getById(id))
        .next()
        .doOnNext(eachAddParent(corpus));
  }

  @GraphQLQuery(name = "relations", description = "Get all relations in the corpus")
  public Flux<BaleenRelation> getAllRelations(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "offset", defaultValue = "0") final int offset,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit,
      @GraphQLArgument(name = "sourceType") final String sourceType,
      @GraphQLArgument(name = "targetType") final String targetType,
      @GraphQLArgument(name = "relationshipType") final String relationshipType,
      @GraphQLArgument(name = "relationshipSubType") final String relationshipSubType,
      @GraphQLArgument(name = "sourceValue") final String sourceValue,
      @GraphQLArgument(name = "targetValue") final String targetValue,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    // Two distinct types of query here really.. one for everything (simple really, listing the db)
    // and one which is really a find query

    if (StringUtils.isEmpty(sourceType) && StringUtils.isEmpty(sourceValue)
        && StringUtils.isEmpty(targetType) && StringUtils.isEmpty(targetValue)
        && StringUtils.isEmpty(relationshipType) && StringUtils.isEmpty(relationshipSubType)) {
      return getProviders(corpus, RelationProvider.class, hints)
          .flatMap(p -> p.getAllRelations(offset, limit))
          .doOnNext(eachAddParent(corpus));

    } else {
      return getProviders(corpus, RelationProvider.class, hints)
          .flatMap(p -> p.getRelationsByMention(sourceValue, sourceType, relationshipType,
              relationshipSubType, targetValue, targetType, offset, limit))
          .doOnNext(eachAddParent(corpus));
    }

  }

  @GraphQLQuery(name = "relationCount",
      description = "Count the number of relations in this corpus")
  public Mono<Long> getDocuments(@GraphQLContext final BaleenCorpus corpus, @GraphQLArgument(
      name = "hints",
      description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, RelationProvider.class, hints).flatMap(RelationProvider::count)
        .reduce(0L, (a, b) -> a + b);
  }
}
