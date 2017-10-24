package io.committed.ketos.plugins.data.baleenmongo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import io.committed.ketos.plugins.data.baleenmongo.repository.BaleenEntitiesRepository;

@Component
public class EntityConverter implements Converter<String, BaleenEntities> {

  @Autowired
  BaleenEntitiesRepository repository;

  @Override
  public BaleenEntities convert(final String id) {
    return repository.findById(id).block();
  }
}
