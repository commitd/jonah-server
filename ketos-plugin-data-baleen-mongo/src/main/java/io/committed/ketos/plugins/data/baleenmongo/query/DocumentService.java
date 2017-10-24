package io.committed.ketos.plugins.data.baleenmongo.query;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.plugins.data.baleenmongo.dao.BaleenDocument;
import io.committed.ketos.plugins.data.baleenmongo.repository.BaleenDocumentRepository;
import io.committed.ketos.plugins.graphql.baleen.Document;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@VesselGraphQlService
public class DocumentService {

  @Autowired
  private BaleenDocumentRepository documents;

  @GraphQLQuery(name = "hello")
  public Flux<String> hello() {
    return Flux.just("a", "good", "night");
  }

  @GraphQLQuery(name = "documents")
  public Flux<Document> getDocuments(
      @GraphQLArgument(name = "limit", defaultValue = "0") final int limit) {
    Flux<BaleenDocument> stream = documents.findAll();
    if (limit > 0) {
      stream = stream.take(limit);
    }

    return stream.map(BaleenDocument::toDocument);
  }

  @GraphQLQuery(name = "documents")
  public Flux<Document> getDocuments(@GraphQLArgument(name = "search") final String search,
      @GraphQLArgument(name = "limit") final int limit) {
    Flux<BaleenDocument> stream = documents.searchDocuments(search);
    if (limit > 0) {
      stream = stream.take(limit);
    }

    return stream.map(BaleenDocument::toDocument);
  }


  @GraphQLQuery(name = "document")
  public Mono<Document> getDocument(
      @GraphQLArgument(name = "id") @GraphQLId final String id) {
    return documents.findByExternalId(id).map(BaleenDocument::toDocument);
  }
}
