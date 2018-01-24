package io.committed.ketos.graphql.baleen.document;

import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;


@GraphQLService
public class DocumentRelationService extends AbstractGraphQlService {

  @Autowired
  public DocumentRelationService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "relations", description = "Get all relations in this document")
  public Flux<BaleenRelation> getRelations(@GraphQLContext final BaleenDocument document,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProvidersFromContext(document, RelationProvider.class, hints)
        .flatMap(p -> p.getRelations(document))
        .doOnNext(eachAddParent(document));

  }


}
