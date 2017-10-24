package io.committed.ketos.plugins.data.baleenmongo.repository;

import java.util.Collection;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import io.committed.ketos.plugins.data.baleenmongo.dao.BaleenRelation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BaleenRelationRepository
    extends ReactiveCrudRepository<BaleenRelation, String> {

  Mono<BaleenRelation> findByExternalId(String id);

  Flux<BaleenRelation> findByDocId(String id);

  Flux<BaleenRelation> deleteByDocId(String id);

  Flux<BaleenRelation> deleteByDocIdIn(Collection<String> ids);

  Flux<BaleenRelation> findBySource(String entityId);

  Flux<BaleenRelation> findByTarget(String entityId);
}
