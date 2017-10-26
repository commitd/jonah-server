package io.committed.vessel.plugin.data.jdbc.repository;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;

import io.committed.vessel.plugin.data.jdbc.dao.SqlEntity;

public interface SqlEntityRepository extends JpaRepository<SqlEntity, Long> {

  Optional<SqlEntity> findByExternalid(String id);

  Stream<SqlEntity> findByDocId(String id);

}
