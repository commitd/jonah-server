package io.committed.ketos.plugins.data.mongo.providers;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.mongo.AbstractMongoDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.plugins.data.mongo.dao.FakeMongoEntities;
import io.committed.ketos.plugins.data.mongo.dao.MongoEntities;
import io.committed.ketos.plugins.data.mongo.repository.BaleenEntitiesRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoEntityProvider extends AbstractMongoDataProvider implements EntityProvider {

  private final BaleenEntitiesRepository entities;

  @Autowired
  public MongoEntityProvider(final String dataset, final String datasource,
      final ReactiveMongoTemplate mongoTemplate, final BaleenEntitiesRepository entities) {
    super(dataset, datasource, mongoTemplate);
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
  public Mono<Long> count() {
    return entities.count();
  }

  @Override
  public Flux<TermBin> countByType() {
    final Aggregation aggregation = newAggregation(unwind("entities"),
        group("entities.type").count().as("count"), project("count").and("_id").as("term"));
    // TODO: FIXME HEre I use FakeMongoEntities which enables Spring to know that the type existings
    // inside entities (by knowing that entities is a list of BaleenMention)
    // however in the rest I use MongoEntities since that allows us to grab anything in the Baleen
    // properties. Maybe there's a hybrid (exist BaleenMention from Document)...
    return getTemplate().aggregate(aggregation, FakeMongoEntities.class, TermBin.class);
  }


  @Override
  public Flux<BaleenEntity> getAll(final int offset, final int limit) {
    return entities.findAll().skip(offset).take(limit).map(MongoEntities::toEntity);
  }
}
