package io.committed.ketos.plugins.data.mongo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import io.committed.ketos.plugins.data.mongo.repository.BaleenEntitiesRepository;

@Component
public class EntityConverter implements Converter<String, MongoEntities> {

  @Autowired
  BaleenEntitiesRepository repository;

  @Override
  public MongoEntities convert(final String id) {
    return repository.findById(id).block();
  }
}
