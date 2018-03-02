package io.committed.ketos.data.elasticsearch.providers;

import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.providers.baleen.CrudEntityProvider;
import io.committed.ketos.common.references.BaleenEntityReference;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;
import io.committed.ketos.data.elasticsearch.repository.EsMentionService;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;
import reactor.core.publisher.Mono;

public class ElasticsearchCrudEntityProvider
    extends AbstractElasticsearchCrudDataProvider<BaleenEntityReference, BaleenEntity>
    implements CrudEntityProvider {

  private final EsEntityService entities;
  private final EsMentionService mentions;
  private final EsRelationService relations;

  public ElasticsearchCrudEntityProvider(final String dataset, final String datasource,
      final EsMentionService mentions, final EsEntityService entities, final EsRelationService relations) {
    super(dataset, datasource);
    this.mentions = mentions;
    this.entities = entities;
    this.relations = relations;
  }

  @Override
  public Mono<Boolean> delete(final BaleenEntityReference reference) {

    delete(mentions, reference.getDocumentId(), BaleenProperties.ENTITY_ID,
        reference.getEntityId());
    delete(relations, reference.getDocumentId(), BaleenProperties.RELATION_SOURCE + "." + BaleenProperties.ENTITY_ID,
        reference.getEntityId());
    delete(relations, reference.getDocumentId(), BaleenProperties.RELATION_TARGET + "." + BaleenProperties.ENTITY_ID,
        reference.getEntityId());

    return Mono.just(delete(entities, reference.getDocumentId(), reference.getEntityId()));
  }

  @Override
  public Mono<Boolean> save(final BaleenEntity item) {
    // TODO Auto-generated method stub
    return null;
  }

}
