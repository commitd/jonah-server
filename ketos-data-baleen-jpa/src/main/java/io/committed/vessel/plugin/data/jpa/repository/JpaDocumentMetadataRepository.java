package io.committed.vessel.plugin.data.jpa.repository;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import io.committed.vessel.plugin.data.jpa.dao.JpaDocumentMetadata;

@NoRepositoryBean
public interface JpaDocumentMetadataRepository
    extends JpaRepository<JpaDocumentMetadata, Long> {

  Stream<JpaDocumentMetadata> findByDocId(String externalId);

}
