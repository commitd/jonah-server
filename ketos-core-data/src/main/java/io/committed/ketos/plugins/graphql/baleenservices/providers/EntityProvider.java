package io.committed.ketos.plugins.graphql.baleenservices.providers;

import io.committed.ketos.plugins.data.baleen.BaleenDocument;
import io.committed.ketos.plugins.data.baleen.BaleenEntity;
import io.committed.ketos.plugins.data.baleen.BaleenMention;
import io.leangen.graphql.annotations.GraphQLContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EntityProvider extends DataProvider {
  Mono<BaleenEntity> getById(final String id);

  Flux<BaleenEntity> getByDocument(@GraphQLContext final BaleenDocument document);

  default Flux<BaleenEntity> getByDocumentAndType(final BaleenDocument document, final String type,
      final int limit) {
    return filterEntities(getByDocument(document), type, null, limit);
  }

  default Flux<BaleenEntity> getByDocumentAndValue(final BaleenDocument document,
      final String value, final int limit) {
    return filterEntities(getByDocument(document), null, value, limit);

  }

  default Flux<BaleenEntity> getByDocumentAndType(final BaleenDocument document, final String type,
      final String value,
      final int limit) {
    return filterEntities(getByDocument(document), type, value, limit);
  }

  default Mono<BaleenEntity> mentionEntity(final BaleenMention mention) {
    return getById(mention.getEntityId());
  }

  static Flux<BaleenEntity> filterEntities(final Flux<BaleenEntity> in, final String type,
      final String value,
      final int limit) {
    Flux<BaleenEntity> flux = in;
    if (type != null) {
      flux = flux.filter(e -> type.equals(e.getType().get()));
    }
    if (value != null) {
      flux = flux.filter(e -> e.getValues().contains(value));
    }
    if (limit > 0) {
      flux = flux.take(limit);
    }
    return flux;
  }
}
