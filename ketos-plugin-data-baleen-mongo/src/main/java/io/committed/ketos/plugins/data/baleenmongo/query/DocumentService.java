package io.committed.query;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.committed.dao.repository.BaleenDocumentRepository;
import io.committed.dto.Document;
import io.committed.ketos.dao.BaleenDocument;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;

@Component
public class DocumentService {

  @Autowired
  BaleenDocumentRepository documents;

  @GraphQLQuery(name = "documents")
  public Stream<Document> getDocuments(
      @GraphQLArgument(name = "limit", defaultValue = "0") int limit) {
    Stream<BaleenDocument> stream = StreamSupport.stream(documents.findAll().spliterator(), false);
    if (limit > 0) {
      stream = stream.limit(limit);
    }

    return stream.map(Document::new);
  }

  @GraphQLQuery(name = "documents")
  public Stream<Document> getDocuments(@GraphQLArgument(name = "search") String search,
      @GraphQLArgument(name = "limit") int limit) {
    Stream<BaleenDocument> stream = documents.searchDocuments(search);
    if (limit > 0) {
      stream = stream.limit(limit);
    }

    return stream.map(Document::new);
  }


  @GraphQLQuery(name = "document")
  public Optional<Document> getDocument(
      @GraphQLArgument(name = "id") @NotNull @GraphQLId String id) {
    return documents.findByExternalId(id).map(Document::new);
  }
}
