package io.committed.ketos.plugins.graphql.baleenservices.providers;

import io.committed.ketos.plugins.data.baleen.BaleenDocument;
import io.committed.ketos.plugins.data.baleen.BaleenMention;
import io.committed.ketos.plugins.data.baleen.BaleenRelation;
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
}
