package io.committed.ketos.common.providers.baleen;


import io.committed.ketos.common.data.BaleenDocument;
import io.committed.vessel.core.dto.analytic.TermBin;
import io.committed.vessel.core.dto.analytic.TimeBin;
import io.committed.vessel.server.data.providers.DataProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DocumentProvider extends DataProvider {

  Mono<BaleenDocument> getById(String id);

  Flux<BaleenDocument> search(String search, int limit);

  Flux<BaleenDocument> all(int limit);

  Mono<Long> count();

  Flux<TermBin> countByType();

  Flux<TimeBin> countByDate();


  @Override
  default String getProviderType() {
    return "DocumentProvider";
  }

  Flux<TermBin> countByClassification();

  Flux<TermBin> countByLanguage();

}
