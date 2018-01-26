package io.committed.ketos.data.jpa.repository;

import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.ketos.data.jpa.dao.JpaDocumentMetadata;

@NoRepositoryBean
public interface JpaDocumentMetadataRepository extends JpaRepository<JpaDocumentMetadata, Long> {

  Stream<JpaDocumentMetadata> findByDocId(String externalId);

  @Query(
      value = "SELECT NEW io.committed.invest.core.dto.analytic.TermBin(d.name, COUNT(d.docId)) from JpaDocumentMetadata d GROUP BY d.name LIMIT ?2")
  Stream<TermBin> countByKey(int limit);

  @Query(
      value = "SELECT NEW io.committed.invest.core.dto.analytic.TermBin(d.name, COUNT(d.docId)) from JpaDocumentMetadata d WHERE d.name = ?1 GROUP BY d.name LIMIT ?2")
  Stream<TermBin> countByKey(String key, int limit);

  @Query(
      value = "SELECT NEW io.committed.invest.core.dto.analytic.TermBin(d.value, COUNT(d)) from JpaDocumentMetadata d GROUP BY d.name LIMIT ?2")
  Stream<TermBin> countByValue(int limit);

  @Query(
      value = "SELECT NEW io.committed.invest.core.dto.analytic.TermBin(d.value, COUNT(d)) from JpaDocumentMetadata d WHERE d.name = ?1 GROUP BY d.name LIMIT ?2")
  Stream<TermBin> countByValue(String key, int limit);

}
