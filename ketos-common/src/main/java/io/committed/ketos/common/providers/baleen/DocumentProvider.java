package io.committed.ketos.common.providers.baleen;


import java.util.List;
import java.util.Optional;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentSearch;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentProbe;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DocumentProvider extends DataProvider {

  @Override
  default String getProviderType() {
    return "DocumentProvider";
  }


  Mono<BaleenDocument> getById(String id);

  Flux<BaleenDocument> getByExample(DocumentProbe probe);

  Mono<Long> count();

  Flux<BaleenDocument> all(int offset, int size);

  DocumentSearchResult search(BaleenDocumentSearch documentSearch, int offset, int size);

  Flux<TermBin> countByField(Optional<DocumentFilter> documentFilter, List<String> path);

  // TODO: Special case, because of the return type... but seems wrong to have this different to
  // above.
  Flux<TimeBin> countByDate(Optional<DocumentFilter> documentFilter);


  // TODO: Delete these

  // Mono<Long> countSearchMatches(String query);
  //
  // Flux<TermBin> countByType();
  //
  // Flux<TermBin> countByClassification();
  //
  // Flux<TermBin> countByLanguage();



}
