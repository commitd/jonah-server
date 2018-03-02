package io.committed.ketos.data.elasticsearch.providers;

import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.CrudRelationProvider;
import io.committed.ketos.common.references.BaleenRelationReference;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;
import reactor.core.publisher.Mono;

public class ElasticsearchCrudRelationProvider
    extends AbstractElasticsearchCrudDataProvider<BaleenRelationReference, BaleenRelation>
    implements CrudRelationProvider {

  private final EsRelationService relations;

  public ElasticsearchCrudRelationProvider(final String dataset, final String datasource,
      final EsRelationService relations) {
    super(dataset, datasource);
    this.relations = relations;
  }

  @Override
  public Mono<Boolean> delete(final BaleenRelationReference reference) {
    return Mono.just(delete(relations, reference.getDocumentId(), reference.getRelationId()));
  }

  @Override
  public Mono<Boolean> save(final BaleenRelation item) {
    return Mono
        .just(relations.updateOrSave(BaleenProperties.EXTERNAL_ID, item.getId(), Converters.toOutputRelation(item)));
  }

}
