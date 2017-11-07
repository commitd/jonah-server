package io.committed.ketos.plugins.data.mongo.providers;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.plugins.data.mongo.dao.MongoEntities;
import io.committed.ketos.plugins.data.mongo.repository.BaleenEntitiesRepository;
import io.committed.vessel.server.data.providers.AbstractDataProvider;
import io.committed.vessel.server.data.providers.DatabaseConstants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoEntityProvider extends AbstractDataProvider implements EntityProvider {

  private final BaleenEntitiesRepository entities;

  @Autowired
  public MongoEntityProvider(final String dataset, final String datasource,
      final BaleenEntitiesRepository entities) {
    super(dataset, datasource);
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
  public String getDatabase() {
    return DatabaseConstants.MONGO;
  }

}
