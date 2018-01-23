package io.committed.ketos.common.providers.baleen;

import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MentionProvider extends DataProvider {

  Mono<BaleenMention> target(BaleenRelation relation);

  Mono<BaleenMention> source(BaleenRelation relation);

  Flux<BaleenMention> getMentionsByDocument(BaleenDocument document);

  Flux<BaleenMention> getByDocumentWithinArea(BaleenDocument document, Double left, Double right,
      Double top, Double bottom, int offset, int limit);


  @Override
  default String getProviderType() {
    return "MentionProvider";
  }


}
