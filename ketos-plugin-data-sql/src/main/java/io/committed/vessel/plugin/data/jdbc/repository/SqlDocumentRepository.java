package io.committed.vessel.plugin.data.jdbc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.committed.vessel.plugin.data.jdbc.dao.SqlDocument;

public interface SqlDocumentRepository extends JpaRepository<SqlDocument, Long> {

  Optional<SqlDocument> findByExternalId(String id);

}
