package io.committed.vessel.plugin.data.jdbc.repository;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;

import io.committed.vessel.plugin.data.jdbc.dao.SqlDocumentMetadata;

public interface SqlDocumentMetadataRepository
    extends JpaRepository<SqlDocumentMetadata, Long> {

  Stream<SqlDocumentMetadata> findByDocId(String externalId);

}
