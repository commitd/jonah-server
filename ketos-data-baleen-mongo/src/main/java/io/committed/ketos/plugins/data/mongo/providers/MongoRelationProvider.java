package io.committed.ketos.plugins.data.mongo.providers;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.plugins.data.mongo.dao.MongoRelation;
import io.committed.ketos.plugins.data.mongo.repository.BaleenRelationRepository;
import io.committed.vessel.server.data.providers.AbstractDataProvider;
import io.committed.vessel.server.data.providers.DatabaseConstants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoRelationProvider extends AbstractDataProvider implements RelationProvider {

  private final BaleenRelationRepository relations;

  @Autowired
  public MongoRelationProvider(final String dataset, final String datasource,
      final BaleenRelationRepository relations) {
    super(dataset, datasource);

    this.relations = relations;
  }

  @Override
  public Flux<BaleenRelation> getAllRelations(final int limit) {
    return toRelations(relations.findAll().take(limit));
  }

  @Override
  public Flux<BaleenRelation> getByDocument(final String id) {
    return toRelations(relations.findByDocId(id));
  }

  @Override
  public Flux<BaleenRelation> getRelations(final BaleenDocument document) {
    return getByDocument(document.getId());
  }

  @Override
  public Flux<BaleenRelation> getSourceRelations(final BaleenMention mention) {
    return toRelations(relations.findBySource(mention.getId()));
  }

  @Override
  public Flux<BaleenRelation> getTargetRelations(final BaleenMention mention) {
    return toRelations(relations.findByTarget(mention.getId()));
  }

  @Override
  public Mono<BaleenRelation> getById(final String id) {
    return relations.findByExternalId(id).map(MongoRelation::toRelation);
  }

  private Flux<BaleenRelation> toRelations(final Flux<MongoRelation> stream) {
    return stream.map(MongoRelation::toRelation);
  }

  @Override
  public String getDatabase() {
    return DatabaseConstants.MONGO;
  }

  @Override
  public Mono<Long> count() {
    return relations.count();
  }

}
