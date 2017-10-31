package io.committed.ketos.graphql.baleen.services;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.core.services.CorpusProviders;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@VesselGraphQlService
public class RelationService extends AbstractGraphQlService {

  @Autowired
  public RelationService(final CorpusProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "relations")
  public Flux<BaleenRelation> getRelations(@GraphQLContext final BaleenDocument document) {
    return getProvidersFromContext(document, RelationProvider.class)
        .flatMap(p -> p.getRelations(document))
        .map(this.addContext(document));

  }

  @GraphQLQuery(name = "sourceOf")
  public Flux<BaleenRelation> getSourceRelations(@GraphQLContext final BaleenMention mention) {
    return getProvidersFromContext(mention, RelationProvider.class)
        .flatMap(p -> p.getSourceRelations(mention))
        .map(this.addContext(mention));
  }

  @GraphQLQuery(name = "targetOf")
  public Flux<BaleenRelation> getTargetRelations(@GraphQLContext final BaleenMention mention) {
    return getProvidersFromContext(mention, RelationProvider.class)
        .flatMap(p -> p.getTargetRelations(mention))
        .map(this.addContext(mention));
  }

  @GraphQLQuery(name = "relation")
  public Mono<BaleenRelation> getById(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLNonNull @GraphQLArgument(name = "id") final String id) {
    return getProviders(corpus, RelationProvider.class)
        .flatMap(p -> p.getById(id))
        .map(this.addContext(corpus))
        .next();
  }

  // FIXME: should be relations - current bug in spqr
  @GraphQLQuery(name = "allRelations")
  public Flux<BaleenRelation> getAllRelations(
      @GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "limit", defaultValue = "10") final int limit) {
    return getProviders(corpus, RelationProvider.class)
        .flatMap(p -> p.getAllRelations(limit))
        .map(this.addContext(corpus));
  }

  // request this via document bean)
  // @GraphQLQuery(name = "allRelations")
  // public Flux<BaleenRelation> getByDocument(
  // @GraphQLContext final BaleenCorpus corpus,
  // @GraphQLNonNull @GraphQLArgument(name = "documentId") @GraphQLId final String id) {
  // return getProviders(corpus, RelationProvider.class)
  // .flatMap(p -> p.getByDocument(id))
  // .map(this.addContext(corpus));
  // }

}
