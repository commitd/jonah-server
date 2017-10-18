package io.committed.ketos.plugins.data.baleenmongo.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import io.committed.ketos.plugins.data.baleenmongo.dao.BaleenRelation;

@Repository
public interface BaleenRelationRepository
    extends PagingAndSortingRepository<BaleenRelation, String> {

  Optional<BaleenRelation> findByExternalId(String id);

  List<BaleenRelation> findByDocId(String id);

  List<BaleenRelation> deleteByDocId(String id);

  List<BaleenRelation> deleteByDocIdIn(Collection<String> ids);

  List<BaleenRelation> findBySource(String entityId);

  List<BaleenRelation> findByTarget(String entityId);
}
