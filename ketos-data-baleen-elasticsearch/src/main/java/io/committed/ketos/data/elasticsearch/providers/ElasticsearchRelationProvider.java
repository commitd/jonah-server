package io.committed.ketos.data.elasticsearch.providers;

import io.committed.invest.server.data.providers.AbstractDataProvider;
import io.committed.invest.server.data.providers.DatabaseConstants;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.data.elasticsearch.dao.EsRelation;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchRelationProvider extends AbstractDataProvider
    implements RelationProvider {

  private final EsRelationService relationService;

  public ElasticsearchRelationProvider(final String dataset, final String datasource,
      final EsRelationService relationService) {
    super(dataset, datasource);
    this.relationService = relationService;
  }

  @Override
  public String getDatabase() {
    return DatabaseConstants.ELASTICSEARCH;
  }


  @Override
  public Flux<BaleenRelation> getAllRelations(final int offset, final int limit) {
    return relationService.findAll(offset, limit)
        .map(EsRelation::toBaleenRelation);

  }

  @Override
  public Flux<BaleenRelation> getByDocument(final String id) {
    return relationService.findByDocument(id)
        .map(EsRelation::toBaleenRelation);
  }

  @Override
  public Flux<BaleenRelation> getRelations(final BaleenDocument document) {
    return relationService.findByDocument(document.getId())
        .map(EsRelation::toBaleenRelation);
  }

  @Override
  public Flux<BaleenRelation> getSourceRelations(final BaleenMention mention) {
    return relationService.findBySource(mention.getId())
        .map(EsRelation::toBaleenRelation);

  }

  @Override
  public Flux<BaleenRelation> getTargetRelations(final BaleenMention mention) {
    return relationService.findByTarget(mention.getId())
        .map(EsRelation::toBaleenRelation);
  }

  @Override
  public Mono<BaleenRelation> getById(final String id) {
    return relationService.getById(id)
        .map(EsRelation::toBaleenRelation);
  }

  @Override
  public Mono<Long> count() {
    return relationService.count();
  }


}
