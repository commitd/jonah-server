package io.committed.ketos.data.jpa.providers;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.jpa.AbstractJpaDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.input.EntityProbe;
import io.committed.ketos.common.graphql.intermediate.EntitySearchResult;
import io.committed.ketos.common.graphql.output.EntitySearch;
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
  public Flux<BaleenEntity> getAll(final int offset, final int limit) {
    return Flux.fromIterable(entities.findAll(PageRequest.of(offset, limit)))
        .map(JpaEntity::toBaleenEntity);

  }

  @Override
  public Mono<Long> count() {
    return Mono.just(entities.count());
  }

  @Override
  public Flux<TermBin> countByField(final Optional<EntityFilter> filter, final List<String> path, final int limit) {

    // Very limited implementation

    if (path.size() == 1 && path.get(0).equals("type")) {
      return Flux.fromStream(entities.countByType());
    }

    return Flux.empty();
  }

  @Override
  public Flux<BaleenEntity> getByExample(final EntityProbe probe, final int offset, final int limit) {
    return Flux.fromIterable(entities.findAll(Example.of(new JpaEntity(probe))))
        .skip(offset)
        .take(limit)
        .map(JpaEntity::toBaleenEntity);
  }

  @Override
  public EntitySearchResult search(final EntitySearch entitySearch, final int offset, final int limit) {
    // TODO Not worth implementing, very unclear what the correct way to map the search into the
    // database it...
    return new EntitySearchResult(Flux.empty(), Mono.empty(), offset, limit);
  }


}
