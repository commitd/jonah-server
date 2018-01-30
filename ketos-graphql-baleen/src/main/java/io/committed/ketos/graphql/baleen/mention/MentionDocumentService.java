package io.committed.ketos.graphql.baleen.mention;

import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.graphql.baleen.entity.EntityDocumentsService;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Mono;


@GraphQLService
public class MentionDocumentService extends AbstractGraphQlService {

  private final EntityDocumentsService edService;

  @Autowired
  public MentionDocumentService(final DataProviders corpusProviders, final EntityDocumentsService edService) {
    super(corpusProviders);
    this.edService = edService;
  }

  @GraphQLQuery(name = "document", description = "Get document for a mention")
  public Mono<BaleenDocument> mentionEntity(@GraphQLContext final BaleenMention mention,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    // Mention -> Entity -> Document !

    return getProvidersFromContext(mention, EntityProvider.class, hints)
        .flatMap(p -> p.getById(mention.getEntityId()))
        .flatMap(e -> edService.getDocumentForEntity(e, hints))
        .next()
        .doOnNext(eachAddParent(mention));
  }


}

