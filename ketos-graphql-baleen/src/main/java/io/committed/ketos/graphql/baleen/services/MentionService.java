package io.committed.ketos.graphql.baleen.services;

import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.invest.server.data.services.DatasetProviders;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@GraphQLService
public class MentionService extends AbstractGraphQlService {

  @Autowired
  public MentionService(final DatasetProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "mentions", description = "Get all mentiosn in the document")
  public Flux<BaleenMention> getMentionsByDocument(@GraphQLContext final BaleenDocument document,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProvidersFromContext(document, MentionProvider.class, hints)
        .flatMap(p -> p.getMentionsByDocument(document)).map(this.addContext(document));
  }

  @GraphQLQuery(name = "source", description = "Get the source entity of this relation")
  public Mono<BaleenMention> source(@GraphQLContext final BaleenRelation relation, @GraphQLArgument(
      name = "hints",
      description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProvidersFromContext(relation, MentionProvider.class, hints)
        .flatMap(p -> p.source(relation)).map(this.addContext(relation))
        // May be many but we only want one (and only should have one per db)
        .next();
  }

  @GraphQLQuery(name = "target", description = "Get the target entity of this relation")
  public Mono<BaleenMention> target(@GraphQLContext final BaleenRelation relation, @GraphQLArgument(
      name = "hints",
      description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProvidersFromContext(relation, MentionProvider.class, hints)
        .flatMap(p -> p.target(relation)).map(this.addContext(relation))
        // May be many but we only want one (and only should have one)
        .next();
  }

}
