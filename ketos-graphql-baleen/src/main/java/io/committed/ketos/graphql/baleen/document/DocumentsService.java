package io.committed.ketos.graphql.baleen.document;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Mono;

@GraphQLService
public class DocumentsService extends AbstractGraphQlService {

  private final io.committed.ketos.graphql.baleen.corpus.CorpusDocumentsService corpusDocumentService;

  @Autowired
  public DocumentsService(final DataProviders corpusProviders,
      final io.committed.ketos.graphql.baleen.corpus.CorpusDocumentsService corpusDocumentService) {
    super(corpusProviders);
    this.corpusDocumentService = corpusDocumentService;
  }

  @GraphQLQuery(name = "document", description = "Document containing the entity")
  public Mono<BaleenDocument> getDocumentForEntity(@GraphQLContext final BaleenEntity entity,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final Optional<BaleenCorpus> corpus = entity.findParent(BaleenCorpus.class);
    if (!corpus.isPresent()) {
      return Mono.empty();
    }

    // Back it off to another service... to save on implementation
    return corpusDocumentService.getDocument(corpus.get(), entity.getDocId(), hints)
        .doOnNext(eachAddParent(entity));
  }

}
