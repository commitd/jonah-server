package io.committed.vessel.plugin.data.jpa.repository;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import io.committed.vessel.core.dto.analytic.TermBin;
import io.committed.vessel.plugin.data.jpa.dao.JpaDocument;

@NoRepositoryBean
public interface JpaDocumentRepository extends JpaRepository<JpaDocument, Long> {

  Optional<JpaDocument> findByExternalId(String id);

  @Query(
      value = "SELECT NEW io.committed.vessel.core.dto.analytic.TermBin(d.type, COUNT(d)) from JpaDocument d GROUP BY d.type")
  Stream<TermBin> countByType();

  @Query(
      value = "SELECT NEW io.committed.vessel.core.dto.analytic.TermBin(TO_CHAR(d.processsed, 'yyyy-MM-dd'), COUNT(d)) from JpaDocument d GROUP BY TO_CHAR(d.processsed, 'yyyy-MM-dd')")
  Stream<TermBin> countByDate();

  @Query(
      value = "SELECT NEW io.committed.vessel.core.dto.analytic.TermBin(d.classification, COUNT(d)) from JpaDocument d GROUP BY d.classification")
  Stream<TermBin> countByClassification();

  @Query(
      value = "SELECT NEW io.committed.vessel.core.dto.analytic.TermBin(d.language, COUNT(d)) from JpaDocument d GROUP BY d.language")
  Stream<TermBin> countByLanguage();


  Stream<JpaDocument> findByContentContaining(String term, Pageable pageable);

  long countByContentContaining(String term);

}
