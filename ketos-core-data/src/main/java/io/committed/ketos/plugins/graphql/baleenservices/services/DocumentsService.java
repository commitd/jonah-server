package io.committed.ketos.plugins.graphql.baleenservices.services;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.plugins.data.baleen.BaleenCorpus;
import io.committed.ketos.plugins.data.baleen.BaleenDocument;
import io.committed.ketos.plugins.graphql.baleenservices.node.BaleenDocuments;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DocumentProvider;
import io.committed.ketos.plugins.providers.services.CorpusProviders;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Mono;

@VesselGraphQlService
public class DocumentsService extends AbstractGraphQlService {


  @Autowired
  public DocumentsService(final CorpusProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "document")
  public Mono<BaleenDocument> getDocument(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "id") final String id) {

    return getProviders(corpus, DocumentProvider.class)
        .flatMap(p -> p.getById(id))
        .map(this.addContext(corpus))
        .next();
  }

  @GraphQLQuery(name = "documents")
  public BaleenDocuments getDocuments(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "search") final String search,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit) {

    return new BaleenDocuments(
        getProviders(corpus, DocumentProvider.class)
            .flatMap(p -> p.search(search, limit))
            .take(limit))
                .addNodeContext(corpus);
  }

  @GraphQLQuery(name = "documents")
  public BaleenDocuments getDocuments(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit) {

    return new BaleenDocuments(
        getProviders(corpus, DocumentProvider.class).flatMap(p -> p.all(limit))
            .take(limit))
                .addNodeContext(corpus);
  }


}
