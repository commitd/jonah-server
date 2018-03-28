package io.committed.ketos.data.elasticsearch.providers;

import java.util.Optional;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.CrudRelationProvider;
import io.committed.ketos.common.references.BaleenRelationReference;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;

/**
 * Elasticsearch CrudRelationProvider.
 *
 */
public class ElasticsearchCrudRelationProvider
    extends AbstractElasticsearchCrudDataProvider<BaleenRelationReference, BaleenRelation>
    implements CrudRelationProvider {

  private final EsRelationService relations;
  private final String documentType;

  public ElasticsearchCrudRelationProvider(final String dataset, final String datasource,
      final String documentType,
      final EsRelationService relations) {
    super(dataset, datasource);
    this.documentType = documentType;
    this.relations = relations;
  }

  @Override
  public boolean delete(final BaleenRelationReference reference) {
    return delete(relations, reference.getDocumentId(), reference.getRelationId());
  }

  @Override
  public boolean save(final BaleenRelation item) {
    return relations.updateOrSave(Optional.of(documentType), Optional.ofNullable(item.getDocId()),
        BaleenProperties.EXTERNAL_ID, item.getId(),
        Converters.toOutputRelation(item));
  }

}
