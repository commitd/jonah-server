package io.committed.ketos.data.elasticsearch.providers;

import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import io.committed.ketos.data.elasticsearch.dao.EsRelation;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchRelationProvider
    extends AbstractElasticsearchServiceDataProvider<EsDocument, EsRelationService>
    implements RelationProvider {

  public ElasticsearchRelationProvider(final String dataset, final String datasource,
      final EsRelationService relationService) {
    super(dataset, datasource, relationService);
  }

  @Override
  public Flux<BaleenRelation> getAllRelations(final int offset, final int limit) {
    return getService().findAll(offset, limit).map(EsRelation::toBaleenRelation);

  }

  @Override
  public Flux<BaleenRelation> getByDocument(final String id) {
    return getService().findByDocument(id).map(EsRelation::toBaleenRelation);
  }

  @Override
  public Flux<BaleenRelation> getRelations(final BaleenDocument document) {
    return getService().findByDocument(document.getId()).map(EsRelation::toBaleenRelation);
  }

  @Override
  public Flux<BaleenRelation> getSourceRelations(final BaleenMention mention) {
    return getService().findBySource(mention.getId()).map(EsRelation::toBaleenRelation);

  }

  @Override
  public Flux<BaleenRelation> getTargetRelations(final BaleenMention mention) {
    return getService().findByTarget(mention.getId()).map(EsRelation::toBaleenRelation);
  }

  @Override
  public Mono<BaleenRelation> getById(final String id) {
    return getService().getById(id).map(EsRelation::toBaleenRelation);
  }

  @Override
  public Mono<Long> count() {
    return getService().count();
  }


}
