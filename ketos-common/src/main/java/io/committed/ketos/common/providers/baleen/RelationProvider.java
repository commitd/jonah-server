package io.committed.ketos.common.providers.baleen;

import java.util.List;
import java.util.Optional;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.input.RelationProbe;
import io.committed.ketos.common.graphql.intermediate.RelationSearchResult;
import io.committed.ketos.common.graphql.output.RelationSearch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Relation query data provider
 *
 */
public interface RelationProvider extends DataProvider {

  Flux<BaleenRelation> getAll(int offset, int size);

  Flux<BaleenRelation> getByDocument(BaleenDocument document);

  Flux<BaleenRelation> getSourceRelations(final BaleenMention mention);

  Flux<BaleenRelation> getTargetRelations(BaleenMention mention);

  Mono<BaleenRelation> getById(String id);

  @Override
  default String getProviderType() {
    return "RelationProvider";
  }

  Mono<Long> count();

  default Flux<BaleenRelation> getByExample(final RelationProbe probe, final int offset, final int size) {
    return search(RelationSearch.builder().relationFilter(probe.toFilter()).build(), offset, size)
        .getResults();
  }

  Flux<TermBin> countByField(Optional<RelationFilter> filter, List<String> path, final int size);

  RelationSearchResult search(final RelationSearch search,
      final int offset,
      final int size);
}
