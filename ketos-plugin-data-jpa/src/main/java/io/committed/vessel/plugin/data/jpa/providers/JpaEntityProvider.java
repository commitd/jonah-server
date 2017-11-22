package io.committed.vessel.plugin.data.jpa.providers;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.vessel.core.dto.analytic.TermBin;
import io.committed.vessel.plugin.data.jpa.dao.JpaEntity;
import io.committed.vessel.plugin.data.jpa.repository.JpaEntityRepository;
import io.committed.vessel.server.data.providers.AbstractDataProvider;
import io.committed.vessel.server.data.providers.DatabaseConstants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class JpaEntityProvider extends AbstractDataProvider implements EntityProvider {

  private final JpaEntityRepository entities;

  public JpaEntityProvider(final String dataset, final String datasource,
      final JpaEntityRepository entities) {
    super(dataset, datasource);
    this.entities = entities;
  }

  @Override
  public Mono<BaleenEntity> getById(final String id) {
    return Mono.justOrEmpty(entities.findInExternalid(id)).map(JpaEntity::toBaleenEntity);
  }

  @Override
  public Flux<BaleenEntity> getByDocument(final BaleenDocument document) {
    return Flux.fromStream(entities.findByDocId(document.getId())).map(JpaEntity::toBaleenEntity);
  }

  @Override
  public String getDatabase() {
    return DatabaseConstants.SQL;
  }

  @Override
  public Mono<Long> count() {
    return Mono.just(entities.count());
  }

  @Override
  public Flux<TermBin> countByType() {
    // TODO Auto-generated method stub
    return Flux.empty();
  }


}
