package io.committed.ketos.common.providers.baleen;

import io.committed.invest.server.data.providers.DataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RelationProvider extends DataProvider {

  Flux<BaleenRelation> getAllRelations(int offset, int limit);

  Flux<BaleenRelation> getByDocument(String id);

  Flux<BaleenRelation> getRelations(BaleenDocument document);

  Flux<BaleenRelation> getSourceRelations(BaleenMention mention);

  Flux<BaleenRelation> getTargetRelations(BaleenMention mention);

  Mono<BaleenRelation> getById(String id);

  Flux<BaleenRelation> getRelationsByMention(String sourceValue, String sourceType,
      String relationshipType, String relationshipSubType, String targetValue, String targetType,
      int offset, int limit);

  @Override
  default String getProviderType() {
    return "RelationProvider";
  }

  Mono<Long> count();


}
