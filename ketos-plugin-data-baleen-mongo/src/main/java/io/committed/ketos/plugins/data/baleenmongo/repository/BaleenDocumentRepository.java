package io.committed.ketos.plugins.data.baleenmongo.repository;

import java.util.Collection;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import io.committed.ketos.plugins.data.baleenmongo.dao.BaleenDocument;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BaleenDocumentRepository
    extends ReactiveCrudRepository<BaleenDocument, String> {

  Mono<BaleenDocument> findByDocumentSource(String path);

  Flux<BaleenDocument> findByDocumentSourceIn(Collection<String> paths, Pageable page);

  Flux<BaleenDocument> findByDocumentSourceIn(Collection<String> paths);

  Flux<BaleenDocument> findByDocumentSourceIn(Collection<String> paths, Sort sort);

  Flux<BaleenDocument> findByDocumentSourceStartsWith(String absolutePath, Pageable page);

  Flux<BaleenDocument> findByDocumentSourceStartsWith(String absolutePath);

  @GraphQLQuery(name = "document")
  Mono<BaleenDocument> findByExternalId(@GraphQLArgument(name = "id") String id);

  @Query(value = "{ $text: { $search: ?0 } }")
  Flux<BaleenDocument> searchDocuments(String terms);


}
