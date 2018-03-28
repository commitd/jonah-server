package io.committed.ketos.graphql.baleen.mention;

import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Mono;


/**
 * Extend mention with document related functionality.
 */
@GraphQLService
public class MentionDocumentService extends AbstractGraphQlService {

  @Autowired
  public MentionDocumentService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "document", description = "Get document for a mention")
  public Mono<BaleenDocument> mentionEntity(@GraphQLContext final BaleenMention mention,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return getProvidersFromContext(mention, DocumentProvider.class, hints)
        .flatMap(p -> p.getById(mention.getDocId()))
        .next()
        .doOnNext(eachAddParent(mention));
  }

}

