package io.committed.ketos.common.providers.baleen;


import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DocumentProvider extends DataProvider {

  Mono<BaleenDocument> getById(String id);

  Flux<BaleenDocument> search(String search, int offset, int size);



  Flux<BaleenDocument> all(int offset, int size);

  Mono<Long> count();

  Flux<TermBin> countByType();

  Flux<TimeBin> countByDate();

  Mono<Long> countSearchMatches(String query);

  Flux<TermBin> countByClassification();

  Flux<TermBin> countByLanguage();

  @Override
  default String getProviderType() {
    return "DocumentProvider";
  }
}
