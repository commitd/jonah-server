package io.committed.ketos.plugins.data.mongo.repository;

import java.util.Collection;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import io.committed.ketos.plugins.data.mongo.dao.MongoDocument;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BaleenDocumentRepository
    extends ReactiveCrudRepository<MongoDocument, String> {

  Mono<MongoDocument> findByDocumentSource(String path);

  Flux<MongoDocument> findByDocumentSourceIn(Collection<String> paths, Pageable page);

  Flux<MongoDocument> findByDocumentSourceIn(Collection<String> paths);

  Flux<MongoDocument> findByDocumentSourceIn(Collection<String> paths, Sort sort);

  Flux<MongoDocument> findByDocumentSourceStartsWith(String absolutePath, Pageable page);

  Flux<MongoDocument> findByDocumentSourceStartsWith(String absolutePath);

  @GraphQLQuery(name = "document")
  Mono<MongoDocument> findByExternalId(@GraphQLArgument(name = "id") String id);

  @Query(value = "{ $text: { $search: ?0 } }")
  Flux<MongoDocument> searchDocuments(String terms);


}
