package io.committed.ketos.plugins.graphql.baleenservices.providers;

import io.committed.ketos.plugins.data.baleen.BaleenDocument;
import io.committed.ketos.plugins.data.baleen.BaleenMention;
import io.committed.ketos.plugins.data.baleen.BaleenRelation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MentionProvider extends DataProvider {

  Mono<BaleenMention> target(BaleenRelation relation);

  Mono<BaleenMention> source(BaleenRelation relation);

  Flux<BaleenMention> getMentionsByDocument(BaleenDocument document);

}
