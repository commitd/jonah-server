package io.committed.ketos.graphql.baleen.document;

import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;


@GraphQLService
public class EntityService extends AbstractGraphQlService {

  @Autowired
  public EntityService(final DataProviders corpusProviders) {
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
          .doOnNext(eachAddParent(document));
    } else if (value != null) {
      return getProvidersFromContext(document, EntityProvider.class, hints)
          .flatMap(p -> p.getByDocumentAndValue(document, value, limit))
          .doOnNext(eachAddParent(document));
    } else if (type != null) {
      return getProvidersFromContext(document, EntityProvider.class, hints)
          .flatMap(p -> p.getByDocumentAndType(document, type, limit))
          .doOnNext(eachAddParent(document));
    } else {
      // Both are null
      return getProvidersFromContext(document, EntityProvider.class, hints)
          .flatMap(p -> p.getByDocument(document).take(limit))
          .doOnNext(eachAddParent(document));
    }

  }



}

