package io.committed.ketos.data.jpa.repository;

import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.ketos.data.jpa.dao.JpaDocument;

@NoRepositoryBean
public interface JpaDocumentRepository extends JpaRepository<JpaDocument, Long>, JpaSpecificationExecutor<JpaDocument> {

  Optional<JpaDocument> findByExternalId(String id);

  @Query(
      value = "SELECT NEW io.committed.invest.core.dto.analytic.TermBin(d.type, COUNT(d)) from JpaDocument d GROUP BY d.type LIMIT ?1")
  Stream<TermBin> countByType(int size);

  @Query(
      value = "SELECT NEW io.committed.invest.core.dto.analytic.TermBin(TO_CHAR(d.processsed, 'yyyy-MM-dd'), COUNT(d)) from JpaDocument d GROUP BY TO_CHAR(d.processsed, 'yyyy-MM-dd')")
  Stream<TermBin> countByDate();

  @Query(
      value = "SELECT NEW io.committed.invest.core.dto.analytic.TermBin(d.classification, COUNT(d)) from JpaDocument d GROUP BY d.classification  LIMIT ?1")
  Stream<TermBin> countByClassification(int size);

  @Query(
      value = "SELECT NEW io.committed.invest.core.dto.analytic.TermBin(d.language, COUNT(d)) from JpaDocument d GROUP BY d.language  LIMIT ?1")
  Stream<TermBin> countByLanguage(int size);


  Stream<JpaDocument> findByContentContaining(String term, Pageable pageable);

  long countByContentContaining(String term);

}
