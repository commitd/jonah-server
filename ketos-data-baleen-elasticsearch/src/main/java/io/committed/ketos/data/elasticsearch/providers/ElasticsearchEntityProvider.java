package io.committed.ketos.data.elasticsearch.providers;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.data.elasticsearch.dao.EsEntity;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;
import io.committed.vessel.core.dto.analytic.TermBin;
import io.committed.vessel.server.data.providers.AbstractDataProvider;
import io.committed.vessel.server.data.providers.DatabaseConstants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchEntityProvider extends AbstractDataProvider
    implements EntityProvider {

  private final EsEntityService entityService;

  public ElasticsearchEntityProvider(final String dataset, final String datasource,
      final EsEntityService entityService) {
    super(dataset, datasource);
    this.entityService = entityService;
  }

  @Override
  public String getDatabase() {
    return DatabaseConstants.ELASTICSEARCH;
  }

  @Override
  public Mono<BaleenEntity> getById(final String id) {
    return entityService.getById(id)
        .map(EsEntity::toBaleenEntity);

  }

  @Override
  public Flux<BaleenEntity> getByDocument(final BaleenDocument document) {
    return entityService.findByDocumentId(document.getId())
        .map(EsEntity::toBaleenEntity);
  }

  @Override
  public Mono<Long> count() {
    return entityService.count();
  }

  @Override
  public Flux<TermBin> countByType() {
    return entityService.countByType();

  }


}
