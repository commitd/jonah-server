package io.committed.ketos.plugins.data.baleenmongo.repository;

import java.util.Collection;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import io.committed.ketos.plugins.data.baleenmongo.dao.MongoRelation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BaleenRelationRepository
    extends ReactiveCrudRepository<MongoRelation, String> {

  Mono<MongoRelation> findByExternalId(String id);

  Flux<MongoRelation> findByDocId(String id);

  Flux<MongoRelation> deleteByDocId(String id);

  Flux<MongoRelation> deleteByDocIdIn(Collection<String> ids);

  Flux<MongoRelation> findBySource(String entityId);

  Flux<MongoRelation> findByTarget(String entityId);
}
