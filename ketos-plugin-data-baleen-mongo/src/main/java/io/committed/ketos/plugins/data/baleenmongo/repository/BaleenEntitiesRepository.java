package io.committed.ketos.plugins.data.baleenmongo.repository;

import java.util.Collection;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import io.committed.ketos.plugins.data.baleenmongo.dao.BaleenEntities;
import reactor.core.publisher.Flux;

@Repository
public interface BaleenEntitiesRepository
    extends ReactiveCrudRepository<BaleenEntities, String> {

  Flux<BaleenEntities> findByDocId(String id);

  Flux<BaleenEntities> deleteByDocId(String id);

  Flux<BaleenEntities> deleteByDocIdIn(Collection<String> ids);

}
