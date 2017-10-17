package io.committed.ketos.plugins.data.baleenmongo.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import io.committed.dao.BaleenDocument;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;

@Repository
public interface BaleenDocumentRepository
    extends PagingAndSortingRepository<BaleenDocument, String> {


  Optional<BaleenDocument> findByDocumentSource(String path);

  Page<BaleenDocument> findByDocumentSourceIn(Collection<String> paths, Pageable page);

  Stream<BaleenDocument> findByDocumentSourceIn(Collection<String> paths);

  Stream<BaleenDocument> findByDocumentSourceIn(Collection<String> paths, Sort sort);

  Page<BaleenDocument> findByDocumentSourceStartsWith(String absolutePath, Pageable page);

  Stream<BaleenDocument> findByDocumentSourceStartsWith(String absolutePath);

  @GraphQLQuery(name = "document")
  Optional<BaleenDocument> findByExternalId(@GraphQLArgument(name = "id") String id);

  @Query(value = "{ $text: { $search: ?0 } }")
  Stream<BaleenDocument> searchDocuments(String terms);


}
