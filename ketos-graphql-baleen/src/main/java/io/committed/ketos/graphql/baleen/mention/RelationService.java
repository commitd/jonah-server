package io.committed.ketos.graphql.baleen.mention;

import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;


@GraphQLService
public class RelationService extends AbstractGraphQlService {

  @Autowired
  public RelationService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }


  @GraphQLQuery(name = "sourceOf", description = "Find relations which have the mention as source")
  public Flux<BaleenRelation> getSourceRelations(@GraphQLContext final BaleenMention mention,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProvidersFromContext(mention, RelationProvider.class, hints)
        .flatMap(p -> p.getSourceRelations(mention))
        .doOnNext(eachAddParent(mention));
  }

  @GraphQLQuery(name = "targetOf", description = "Find relations which have the mention as target")
  public Flux<BaleenRelation> getTargetRelations(@GraphQLContext final BaleenMention mention,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProvidersFromContext(mention, RelationProvider.class, hints)
        .flatMap(p -> p.getTargetRelations(mention))
        .doOnNext(eachAddParent(mention));
  }


}
