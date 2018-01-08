package io.committed.ketos.data.elasticsearch.providers;

import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import io.committed.ketos.data.elasticsearch.dao.EsEntity;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchMentionProvider
    extends AbstractElasticsearchServiceDataProvider<EsDocument, EsEntityService>
    implements MentionProvider {

  public ElasticsearchMentionProvider(final String dataset, final String datasource,
      final EsEntityService mentionService) {
    super(dataset, datasource, mentionService);
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
    return getService().findByDocumentId(document.getId()).map(EsEntity::toBaleenMention);
  }

  private Mono<BaleenMention> getMentionFromEntity(final String entityId) {
    return getService().getById(entityId).map(EsEntity::toBaleenMention);
  }

}
