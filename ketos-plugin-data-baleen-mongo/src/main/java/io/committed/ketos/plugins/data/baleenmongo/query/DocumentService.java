package io.committed.ketos.plugins.data.baleenmongo.query;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.plugins.data.baleenmongo.dao.BaleenDocument;
import io.committed.ketos.plugins.data.baleenmongo.dto.Document;
import io.committed.ketos.plugins.data.baleenmongo.repository.BaleenDocumentRepository;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;

@VesselGraphQlService
public class DocumentService {

  @Autowired
  private BaleenDocumentRepository documents;

  @GraphQLQuery(name = "documents")
  public Stream<Document> getDocuments(
      @GraphQLArgument(name = "limit", defaultValue = "0") final int limit) {
    Stream<BaleenDocument> stream = StreamSupport.stream(documents.findAll().spliterator(), false);
    if (limit > 0) {
      stream = stream.limit(limit);
    }

    return stream.map(BaleenDocument::toDocument);
  }

  @GraphQLQuery(name = "documents")
  public Stream<Document> getDocuments(@GraphQLArgument(name = "search") final String search,
      @GraphQLArgument(name = "limit") final int limit) {
    Stream<BaleenDocument> stream = documents.searchDocuments(search);
    if (limit > 0) {
      stream = stream.limit(limit);
    }

    return stream.map(BaleenDocument::toDocument);
  }


  @GraphQLQuery(name = "document")
  public Optional<Document> getDocument(
      @GraphQLArgument(name = "id") @GraphQLId final String id) {
    return documents.findByExternalId(id).map(BaleenDocument::toDocument);
  }
}
