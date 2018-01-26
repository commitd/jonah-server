package io.committed.ketos.graphql.baleen.document;

import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.input.EntityProbe;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;


@GraphQLService
public class DocumentEntityService extends AbstractGraphQlService {

  @Autowired
  public DocumentEntityService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  // Extend document

  @GraphQLQuery(name = "entities", description = "Get all entities")
  public Flux<BaleenEntity> getByDocument(@GraphQLContext final BaleenDocument document,
      @GraphQLArgument(name = "probe", description = "The type of the entity") final EntityProbe probe,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {


    if (probe != null) {
      probe.setDocId(document.getId());

      return getProvidersFromContext(document, EntityProvider.class, hints)
          .flatMap(p -> p.getByExample(probe, limit))
          .doOnNext(eachAddParent(document));
    } else {

      return getProvidersFromContext(document, EntityProvider.class, hints)
          .flatMap(p -> p.getByDocument(document, limit))
          .doOnNext(eachAddParent(document));
    }



  }



}

