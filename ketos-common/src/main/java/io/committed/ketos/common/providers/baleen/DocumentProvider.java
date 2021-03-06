package io.committed.ketos.common.providers.baleen;

import java.util.List;
import java.util.Optional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.invest.core.constants.TimeInterval;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.analytic.TimeRange;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.ketos.common.constants.ItemTypes;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.general.NamedGeoLocation;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentProbe;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import io.committed.ketos.common.graphql.output.DocumentSearch;

/** Documents query data provider */
public interface DocumentProvider extends DataProvider {

  @Override
  default String getProviderType() {
    return "DocumentProvider";
  }

  Mono<BaleenDocument> getById(String id);

  default Flux<BaleenDocument> getByExample(
      final DocumentProbe probe, final int offset, final int size) {
    return search(
            DocumentSearch.builder().documentFilter(probe.toDocumentFilter()).build(), offset, size)
        .getResults();
  }

  Mono<Long> count();

  Flux<BaleenDocument> getAll(int offset, int size);

  DocumentSearchResult search(DocumentSearch documentSearch, int offset, int size);

  Flux<TermBin> countByField(Optional<DocumentFilter> documentFilter, List<String> path, int size);

  Flux<TimeBin> countByDate(Optional<DocumentFilter> documentFilter, final TimeInterval interval);

  Flux<TermBin> countByJoinedField(
      Optional<DocumentFilter> documentFilter, ItemTypes joinedType, List<String> path, int size);

  Flux<TimeBin> countByJoinedDate(
      Optional<DocumentFilter> documentFilter, ItemTypes joinedType, TimeInterval interval);

  Flux<NamedGeoLocation> documentLocations(Optional<DocumentFilter> documentFilter, int size);

  Mono<TimeRange> documentTimeRange(Optional<DocumentFilter> documentFilter);

  Mono<TimeRange> entityTimeRange(Optional<DocumentFilter> documentFilter);
}
