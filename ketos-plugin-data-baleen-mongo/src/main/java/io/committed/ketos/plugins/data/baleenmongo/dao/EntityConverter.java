package io.committed.ketos.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import io.committed.dao.repository.BaleenEntitiesRepository;

@Component
public class EntityConverter implements Converter<String, BaleenEntities> {

  @Autowired
  BaleenEntitiesRepository repository;

  @Override
  public BaleenEntities convert(String id) {
    return repository.findOne(id);
  }
}
