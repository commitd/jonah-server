package io.committed.ketos.plugins.data.mongo.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import io.committed.invest.support.data.mongo.AbstractMongoDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.plugins.data.mongo.dao.MongoEntities;
import io.committed.ketos.plugins.data.mongo.repository.BaleenEntitiesRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoMentionProvider extends AbstractMongoDataProvider implements MentionProvider {


  private final BaleenEntitiesRepository entities;

  @Autowired
  public MongoMentionProvider(final String dataset, final String datasource,
      final ReactiveMongoTemplate mongoTemplate, final BaleenEntitiesRepository entities) {
    super(dataset, datasource, mongoTemplate);

    this.entities = entities;
  }

  @Override
  public Flux<BaleenMention> getMentionsByDocument(final BaleenDocument document) {
    return getMentionsByDocumentId(document.getId());
  }

  @Override
  public Mono<BaleenMention> source(final BaleenRelation relation) {
    return relationMentionById(relation, relation.getSourceId());
  }

  @Override
  public Mono<BaleenMention> target(final BaleenRelation relation) {
    return relationMentionById(relation, relation.getTargetId());
  }

  private Flux<BaleenEntity> getByDocumentId(final String id) {
    return entities.findByDocId(id).map(MongoEntities::toEntity);
  }

  private Flux<BaleenMention> getMentionsByDocumentId(final String documentId) {
    return getByDocumentId(documentId)
        .flatMap(e -> e.getMentions());
  }

  private Mono<BaleenMention> relationMentionById(final BaleenRelation relation,
      final String sourceId) {
    return getMentionsByDocumentId(relation.getDocId())
        .filter(m -> sourceId.equals(m.getId()))
        .next();
  }

  @Override
  public Flux<BaleenMention> getByDocumentWithinArea(final BaleenDocument document,
      final Double left, final Double right, final Double top, final Double bottom,
      final int offset, final int limit) {
    // TODO Auto-generated method stub
    return getMentionsByDocument(document);
  }
}
