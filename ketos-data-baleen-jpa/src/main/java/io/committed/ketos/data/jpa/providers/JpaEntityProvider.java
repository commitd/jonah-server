package io.committed.ketos.data.jpa.providers;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.jpa.AbstractJpaDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.data.jpa.dao.JpaEntity;
import io.committed.ketos.data.jpa.repository.JpaEntityRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class JpaEntityProvider extends AbstractJpaDataProvider implements EntityProvider {

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
  public Mono<Long> count() {
    return Mono.just(entities.count());
  }

  @Override
  public Flux<TermBin> countByType() {
    return Flux.fromStream(entities.countByType());
  }


}
