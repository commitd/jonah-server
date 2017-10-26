package io.committed.vessel.plugin.data.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.committed.vessel.plugin.data.jpa.dao.JpaDocument;

public interface JpaDocumentRepository extends JpaRepository<JpaDocument, Long> {

  Optional<JpaDocument> findByExternalId(String id);

}
