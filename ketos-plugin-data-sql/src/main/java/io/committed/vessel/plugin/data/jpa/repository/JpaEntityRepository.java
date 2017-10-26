package io.committed.vessel.plugin.data.jpa.repository;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;

import io.committed.vessel.plugin.data.jpa.dao.JpaEntity;

public interface JpaEntityRepository extends JpaRepository<JpaEntity, Long> {

  Optional<JpaEntity> findByExternalid(String id);

  Stream<JpaEntity> findByDocId(String id);

}
