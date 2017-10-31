package io.committed.ketos.common.providers.baleen;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MentionProvider extends DataProvider {

  Mono<BaleenMention> target(BaleenRelation relation);

  Mono<BaleenMention> source(BaleenRelation relation);

  Flux<BaleenMention> getMentionsByDocument(BaleenDocument document);

  @Override
  default String getProviderType() {
    return "MentionProvider";
  }
}
