package io.committed.ketos.graphql.baleen.mention;

import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Mono;


/**
 * Extend mention with entity related functionality.
 */
@GraphQLService
public class MentionEntityService extends AbstractGraphQlService {

  @Autowired
  public MentionEntityService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "entity", description = "Get entities for a mention")
  public Mono<BaleenEntity> mentionEntity(@GraphQLContext final BaleenMention mention,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProvidersFromContext(mention, EntityProvider.class, hints)
        .flatMap(p -> p.mentionEntity(mention))
        .next()
        .doOnNext(eachAddParent(mention));
  }


}

