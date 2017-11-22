package io.committed.ketos.plugins.data.mongo.repository;

import java.util.Collection;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import io.committed.ketos.plugins.data.mongo.dao.MongoDocument;
import io.leangen.graphql.annotations.GraphQLArgument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@NoRepositoryBean
public interface BaleenDocumentRepository
    extends ReactiveCrudRepository<MongoDocument, String> {

  Mono<MongoDocument> findByDocumentSource(String path);

  Flux<MongoDocument> findByDocumentSourceIn(Collection<String> paths, Pageable page);

  Flux<MongoDocument> findByDocumentSourceIn(Collection<String> paths);

  Flux<MongoDocument> findByDocumentSourceIn(Collection<String> paths, Sort sort);

  Flux<MongoDocument> findByDocumentSourceStartsWith(String absolutePath, Pageable page);

  Flux<MongoDocument> findByDocumentSourceStartsWith(String absolutePath);

  Mono<MongoDocument> findByExternalId(@GraphQLArgument(name = "id") String id);

  @Query(value = "{ $text: { $search: ?0 } }")
  Flux<MongoDocument> searchDocuments(String terms);

  @Query(value = "{ $text: { $search: ?0 } }", count = true)
  Mono<Long> countSearchDocuments(String terms);
}
