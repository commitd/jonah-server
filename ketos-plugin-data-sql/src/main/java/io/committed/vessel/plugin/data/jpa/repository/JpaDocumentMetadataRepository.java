package io.committed.vessel.plugin.data.jpa.repository;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;

import io.committed.vessel.plugin.data.jpa.dao.JpaDocumentMetadata;

public interface JpaDocumentMetadataRepository
    extends JpaRepository<JpaDocumentMetadata, Long> {

  Stream<JpaDocumentMetadata> findByDocId(String externalId);

}
