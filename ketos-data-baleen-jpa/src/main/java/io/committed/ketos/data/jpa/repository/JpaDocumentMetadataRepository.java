package io.committed.ketos.data.jpa.repository;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.ketos.data.jpa.dao.JpaDocumentMetadata;

@NoRepositoryBean
public interface JpaDocumentMetadataRepository
    extends JpaRepository<JpaDocumentMetadata, Long> {

  Stream<JpaDocumentMetadata> findByDocId(String externalId);

  @Query(
      value = "SELECT NEW io.committed.invest.core.dto.analytic.TermBin(d.name, COUNT(d.docId)) from JpaDocumentMetadata d GROUP BY d.name")
  Stream<TermBin> countByKey();

  @Query(
      value = "SELECT NEW io.committed.invest.core.dto.analytic.TermBin(d.name, COUNT(d.docId)) from JpaDocumentMetadata d WHERE d.name = ?1 GROUP BY d.name")
  Stream<TermBin> countByKey(String key);

  @Query(
      value = "SELECT NEW io.committed.invest.core.dto.analytic.TermBin(d.value, COUNT(d)) from JpaDocumentMetadata d GROUP BY d.name")
  Stream<TermBin> countByValue();

  @Query(
      value = "SELECT NEW io.committed.invest.core.dto.analytic.TermBin(d.value, COUNT(d)) from JpaDocumentMetadata d WHERE d.name = ?1 GROUP BY d.name")
  Stream<TermBin> countByValue(String key);

}
