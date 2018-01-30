package io.committed.ketos.graphql.baleen.relation;

import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Mono;


@GraphQLService
public class RelationMentionService extends AbstractGraphQlService {

  @Autowired
  public RelationMentionService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "source", description = "Get the source entity of this relation")
  public Mono<BaleenMention> source(@GraphQLContext final BaleenRelation relation, @GraphQLArgument(
      name = "hints",
      description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProvidersFromContext(relation, MentionProvider.class, hints)
        .flatMap(p -> p.source(relation))
        // May be many but we only want one (and only should have one per db)
        .next()
        .doOnNext(eachAddParent(relation));
  }

  @GraphQLQuery(name = "target", description = "Get the target entity of this relation")
  public Mono<BaleenMention> target(@GraphQLContext final BaleenRelation relation, @GraphQLArgument(
      name = "hints",
      description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProvidersFromContext(relation, MentionProvider.class, hints)
        .flatMap(p -> p.target(relation))
        // May be many but we only want one (and only should have one)
        .next()
        .doOnNext(eachAddParent(relation));
  }

}