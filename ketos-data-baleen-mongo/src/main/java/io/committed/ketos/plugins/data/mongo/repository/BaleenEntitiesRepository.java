package io.committed.ketos.plugins.data.mongo.repository;

import java.util.Collection;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import io.committed.ketos.plugins.data.mongo.dao.MongoEntities;
import reactor.core.publisher.Flux;

@NoRepositoryBean
public interface BaleenEntitiesRepository extends ReactiveCrudRepository<MongoEntities, String> {

  Flux<MongoEntities> findByDocId(String id);

  Flux<MongoEntities> deleteByDocId(String id);

  Flux<MongoEntities> deleteByDocIdIn(Collection<String> ids);

}
