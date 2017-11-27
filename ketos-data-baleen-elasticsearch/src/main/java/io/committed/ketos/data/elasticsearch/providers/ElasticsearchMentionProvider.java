package io.committed.ketos.data.elasticsearch.providers;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.data.elasticsearch.dao.EsEntity;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;
import io.committed.vessel.server.data.providers.AbstractDataProvider;
import io.committed.vessel.server.data.providers.DatabaseConstants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchMentionProvider extends AbstractDataProvider
    implements MentionProvider {

  private final EsEntityService entityService;

  public ElasticsearchMentionProvider(final String dataset, final String datasource,
      final EsEntityService mentionService) {
    super(dataset, datasource);
    this.entityService = mentionService;
  }

  @Override
  public String getDatabase() {
    return DatabaseConstants.ELASTICSEARCH;
  }

  @Override
  public Mono<BaleenMention> target(final BaleenRelation relation) {
    return getMentionFromEntity(relation.getTargetId());
  }

  @Override
  public Mono<BaleenMention> source(final BaleenRelation relation) {
    relation.getSourceId();
    return getMentionFromEntity(relation.getSourceId());
  }

  @Override
  public Flux<BaleenMention> getMentionsByDocument(final BaleenDocument document) {
    return entityService.findByDocumentId(document.getId())
        .map(EsEntity::toBaleenMention);
  }

  private Mono<BaleenMention> getMentionFromEntity(final String entityId) {
    return entityService.getById(entityId)
        .map(EsEntity::toBaleenMention);
  }

}
