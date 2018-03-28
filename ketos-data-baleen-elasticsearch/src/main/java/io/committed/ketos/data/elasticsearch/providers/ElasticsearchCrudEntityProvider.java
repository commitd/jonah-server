package io.committed.ketos.data.elasticsearch.providers;

import java.util.Optional;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.providers.baleen.CrudEntityProvider;
import io.committed.ketos.common.references.BaleenEntityReference;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;
import io.committed.ketos.data.elasticsearch.repository.EsMentionService;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;

/**
 * Elasticsearch CrudEntityProvider.
 *
 */
public class ElasticsearchCrudEntityProvider
    extends AbstractElasticsearchCrudDataProvider<BaleenEntityReference, BaleenEntity>
    implements CrudEntityProvider {

  private final EsEntityService entities;
  private final EsMentionService mentions;
  private final EsRelationService relations;
  private final String documentType;

  public ElasticsearchCrudEntityProvider(final String dataset, final String datasource,
      final String documentType,
      final EsMentionService mentions, final EsEntityService entities, final EsRelationService relations) {
    super(dataset, datasource);
    this.documentType = documentType;
    this.mentions = mentions;
    this.entities = entities;
    this.relations = relations;
  }

  @Override
  public boolean delete(final BaleenEntityReference reference) {

    delete(mentions, reference.getDocumentId(), BaleenProperties.ENTITY_ID,
        reference.getEntityId());
    delete(relations, reference.getDocumentId(), BaleenProperties.RELATION_SOURCE + "." + BaleenProperties.ENTITY_ID,
        reference.getEntityId());
    delete(relations, reference.getDocumentId(), BaleenProperties.RELATION_TARGET + "." + BaleenProperties.ENTITY_ID,
        reference.getEntityId());

    return delete(entities, reference.getDocumentId(), reference.getEntityId());
  }


  @Override
  public boolean save(final BaleenEntity item) {
    return entities.updateOrSave(Optional.of(documentType), Optional.ofNullable(item.getDocId()),
        BaleenProperties.EXTERNAL_ID,
        item.getId(),
        Converters.toOutputEntity(item));
  }

}
