package io.committed.ketos.graphql.baleen.relation;

import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Mono;


@GraphQLService
public class RelationDocumentService extends AbstractGraphQlService {

  @Autowired
  public RelationDocumentService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "document", description = "Get document for a relation")
  public Mono<BaleenDocument> document(@GraphQLContext final BaleenRelation relation,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return getProvidersFromContext(relation, DocumentProvider.class, hints)
        .flatMap(p -> p.getById(relation.getDocId()))
        .next()
        .doOnNext(eachAddParent(relation));
  }

}

