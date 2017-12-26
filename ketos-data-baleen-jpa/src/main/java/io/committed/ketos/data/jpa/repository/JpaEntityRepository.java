package io.committed.ketos.data.jpa.repository;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.ketos.data.jpa.dao.JpaEntity;

@NoRepositoryBean
public interface JpaEntityRepository extends JpaRepository<JpaEntity, Long> {

  Optional<JpaEntity> findInExternalid(String id);

  Stream<JpaEntity> findByDocId(String id);

  @Query(
      value = "SELECT NEW io.committed.invest.core.dto.analytic.TermBin(d.type, COUNT(d)) from JpaDocument d GROUP BY d.type")
  Stream<TermBin> countByType();

}
