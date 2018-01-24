package io.committed.ketos.common.providers.baleen;


import java.util.List;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentSearch;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentProbe;
import io.committed.ketos.common.graphql.input.DocumentSearchResult;
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

  Flux<TermBin> countByField(DocumentFilter documentFilter, List<String> path);

  // TODO: Special case, becasuse of the return type... but seems wrong
  Flux<TimeBin> countByDate(DocumentFilter documentFilter);


  // TODO: Delete these

  // Mono<Long> countSearchMatches(String query);
  //
  // Flux<TermBin> countByType();
  //
  // Flux<TermBin> countByClassification();
  //
  // Flux<TermBin> countByLanguage();



}
