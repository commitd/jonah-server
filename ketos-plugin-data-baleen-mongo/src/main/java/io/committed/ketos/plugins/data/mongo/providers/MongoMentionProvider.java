package io.committed.ketos.plugins.data.mongo.providers;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.plugins.data.baleen.BaleenDocument;
import io.committed.ketos.plugins.data.baleen.BaleenEntity;
import io.committed.ketos.plugins.data.baleen.BaleenMention;
import io.committed.ketos.plugins.data.baleen.BaleenRelation;
import io.committed.ketos.plugins.data.mongo.dao.MongoEntities;
import io.committed.ketos.plugins.data.mongo.repository.BaleenEntitiesRepository;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DatasourceConstants;
import io.committed.ketos.plugins.graphql.baleenservices.providers.MentionProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoMentionProvider implements MentionProvider {

  private final String corpus;

  private final BaleenEntitiesRepository entities;

  @Autowired
  public MongoMentionProvider(final String corpus, final BaleenEntitiesRepository entities) {
    this.corpus = corpus;
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
    return getByDocumentId(documentId).flatMap(e -> Flux.fromIterable(e.getMentions()));
  }

  private Mono<BaleenMention> relationMentionById(final BaleenRelation relation,
      final String sourceId) {
    return getMentionsByDocumentId(relation.getDocId()).filter(m -> sourceId.equals(m.getId()))
        .next();
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