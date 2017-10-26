package io.committed.ketos.plugins.graphql.baleenservices.services;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.plugins.data.baleen.BaleenDocument;
import io.committed.ketos.plugins.data.baleen.BaleenMention;
import io.committed.ketos.plugins.data.baleen.BaleenRelation;
import io.committed.ketos.plugins.graphql.baleenservices.providers.MentionProvider;
import io.committed.ketos.plugins.providers.services.CorpusProviders;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@VesselGraphQlService
public class MentionService extends AbstractGraphQlService {

  @Autowired
  public MentionService(final CorpusProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "mentions")
  public Flux<BaleenMention> getMentionsByDocument(@GraphQLContext final BaleenDocument document) {
    return getProvidersFromContext(document, MentionProvider.class)
        .flatMap(p -> p.getMentionsByDocument(document))
        .map(this.addContext(document));
  }

  @GraphQLQuery(name = "source")
  public Mono<BaleenMention> source(@GraphQLContext final BaleenRelation relation) {
    return getProvidersFromContext(relation, MentionProvider.class)
        .flatMap(p -> p.source(relation))
        .map(this.addContext(relation))
        // May be many but we only want one (and only should have one per db)
        .next();
  }

  @GraphQLQuery(name = "target")
  public Mono<BaleenMention> target(@GraphQLContext final BaleenRelation relation) {
    return getProvidersFromContext(relation, MentionProvider.class)
        .flatMap(p -> p.target(relation))
        .map(this.addContext(relation))
        // May be many but we only want one (and only should have one)
        .next();
  }

}
