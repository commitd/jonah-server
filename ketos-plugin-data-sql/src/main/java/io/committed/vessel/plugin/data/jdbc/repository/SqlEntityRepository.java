package io.committed.vessel.plugin.data.jdbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import io.committed.vessel.plugin.data.jdbc.dao.SqlDocument;

public interface SqlEntityRepository extends ReactiveCrudRepository<SqlDocument, Long> {

}
