package io.committed.ketos.plugins.data.mongo.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import io.committed.invest.support.data.mongo.AbstractMongoDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.plugins.data.mongo.dao.MongoRelation;
import io.committed.ketos.plugins.data.mongo.repository.BaleenRelationRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoRelationProvider extends AbstractMongoDataProvider implements RelationProvider {

  private final BaleenRelationRepository relations;

  @Autowired
  public MongoRelationProvider(final String dataset, final String datasource,
      final ReactiveMongoTemplate mongoTemplate, final BaleenRelationRepository relations) {
    super(dataset, datasource, mongoTemplate);

    this.relations = relations;
  }

  @Override
  public Flux<BaleenRelation> getAllRelations(final int offset, final int limit) {
    // TODO: Move to query, not take/skip
    return toRelations(relations.findAll().skip(offset).take(limit));
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
  public Mono<Long> count() {
    return relations.count();
  }

  // TODO: Should the query by example a proper interface here... it's much more sensible from a
  // provider use angle. We could still keep the other stuff to.
  @Override
  public Flux<BaleenRelation> getRelationsByMention(final String sourceValue,
      final String sourceType, final String relationshipType, final String relationshipSubType,
      final String targetValue, final String targetType, final int offset, final int limit) {
    final MongoRelation r = new MongoRelation();
    r.setSourceType(sourceType);
    r.setSourceValue(sourceValue);
    r.setTargetType(targetType);
    r.setTargetValue(targetValue);
    r.setRelationshipType(relationshipType);
    r.setRelationSubtype(relationshipSubType);

    // WE don't have the _class in db
    final ExampleMatcher exampleMatcher = ExampleMatcher.matching().withIgnorePaths("_class");
    return relations.findAll(Example.of(r, exampleMatcher)).skip(offset).take(limit)
        .map(MongoRelation::toRelation);
  }

}
