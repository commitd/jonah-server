package io.committed.ketos.data.elasticsearch.providers;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.extensions.data.providers.DatabaseConstants;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import io.committed.ketos.data.elasticsearch.dao.EsEntity;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchEntityProvider
    extends AbstractElasticsearchServiceDataProvider<EsDocument, EsEntityService>
    implements EntityProvider {

  public ElasticsearchEntityProvider(final String dataset, final String datasource,
      final EsEntityService entityService) {
    super(dataset, datasource, entityService);
  }

  @Override
  public String getDatabase() {
    return DatabaseConstants.ELASTICSEARCH;
  }

  @Override
  public Flux<BaleenEntity> getAll(final int offset, final int limit) {
    return getService().findAll(offset, limit).map(EsEntity::toBaleenEntity);
  }

  @Override
  public Mono<BaleenEntity> getById(final String id) {
    return getService().getById(id).map(EsEntity::toBaleenEntity);

  }

  @Override
  public Flux<BaleenEntity> getByDocument(final BaleenDocument document) {
    return getService().findByDocumentId(document.getId()).map(EsEntity::toBaleenEntity);
  }

  @Override
  public Mono<Long> count() {
    return getService().count();
  }

  @Override
  public Flux<TermBin> countByType() {
    return getService().countByType();

  }


}
