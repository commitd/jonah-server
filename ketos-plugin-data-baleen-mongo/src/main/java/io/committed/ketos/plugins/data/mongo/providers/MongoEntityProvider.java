package io.committed.ketos.plugins.data.mongo.providers;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.plugins.data.baleen.BaleenDocument;
import io.committed.ketos.plugins.data.baleen.BaleenEntity;
import io.committed.ketos.plugins.data.mongo.dao.MongoEntities;
import io.committed.ketos.plugins.data.mongo.repository.BaleenEntitiesRepository;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DatasourceConstants;
import io.committed.ketos.plugins.graphql.baleenservices.providers.EntityProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoEntityProvider implements EntityProvider {

  private final BaleenEntitiesRepository entities;
  private final String corpus;

  @Autowired
  public MongoEntityProvider(final String corpus, final BaleenEntitiesRepository entities) {
    this.corpus = corpus;
    this.entities = entities;
  }

  @Override
  public Mono<BaleenEntity> getById(final String id) {
    return entities.findById(id).map(MongoEntities::toEntity);
  }

  @Override
  public Flux<BaleenEntity> getByDocument(final BaleenDocument document) {
    return entities.findByDocId(document.getId()).map(MongoEntities::toEntity);
  }


  @Override
  public String getDatasource() {
    return DatasourceConstants.MONGO;
  }

  @Override
  public String getCorpus() {
    return corpus;
  }

}
