package io.committed.ketos.plugins.data.baleenmongo.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import io.committed.dao.BaleenEntities;

@Repository
public interface BaleenEntitiesRepository
    extends PagingAndSortingRepository<BaleenEntities, String> {

  List<BaleenEntities> findByDocId(String id);

  List<BaleenEntities> deleteByDocId(String id);

  List<BaleenEntities> deleteByDocIdIn(Collection<String> ids);

}
