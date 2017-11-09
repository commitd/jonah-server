package io.committed.ketos.common.providers.baleen;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.vessel.server.data.providers.DataProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RelationProvider extends DataProvider {

  Flux<BaleenRelation> getAllRelations(int limit);

  Flux<BaleenRelation> getByDocument(String id);

  Flux<BaleenRelation> getRelations(BaleenDocument document);

  Flux<BaleenRelation> getSourceRelations(BaleenMention mention);

  Flux<BaleenRelation> getTargetRelations(BaleenMention mention);

  Mono<BaleenRelation> getById(String id);

  @Override
  default String getProviderType() {
    return "RelationProvider";
  }

  Mono<Long> count();
}
