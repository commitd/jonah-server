package io.committed.ketos.common.providers.baleen;

import java.util.List;
import java.util.Optional;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.input.EntityProbe;
import io.committed.ketos.common.graphql.intermediate.EntitySearchResult;
import io.committed.ketos.common.graphql.output.EntitySearch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EntityProvider extends DataProvider {

  Mono<BaleenEntity> getById(final String id);

  Flux<BaleenEntity> getByDocument(final BaleenDocument document);

  Flux<TermBin> countByField(Optional<EntityFilter> filter, List<String> path, final int limit);

  Flux<BaleenEntity> getAll(final int offset, final int limit);

  default Flux<BaleenEntity> getByExample(final EntityProbe probe, final int offset, final int limit) {
    return search(EntitySearch.builder().entityFilter(probe.toFilter()).build(), offset, limit)
        .getResults();
  }


  EntitySearchResult search(final EntitySearch entitySearch,
      final int offset,
      final int limit);


  Mono<Long> count();


  default Mono<BaleenEntity> mentionEntity(final BaleenMention mention) {
    return getById(mention.getEntityId());
  }

  @Override
  default String getProviderType() {
    return "EntityProvider";
  }

}
