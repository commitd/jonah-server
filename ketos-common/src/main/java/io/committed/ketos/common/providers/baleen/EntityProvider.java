package io.committed.ketos.common.providers.baleen;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.leangen.graphql.annotations.GraphQLContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EntityProvider extends DataProvider {
  Mono<BaleenEntity> getById(final String id);

  Flux<BaleenEntity> getByDocument(@GraphQLContext final BaleenDocument document);

  default Flux<BaleenEntity> getByDocument(@GraphQLContext final BaleenDocument document,
      final int limit) {
    return getByDocument(document).take(limit);
  }

  default Flux<BaleenEntity> getByDocumentAndType(final BaleenDocument document, final String type,
      final int limit) {
    return filterEntities(getByDocument(document), type, null, limit);
  }

  default Flux<BaleenEntity> getByDocumentAndValue(final BaleenDocument document,
      final String value, final int limit) {
    return filterEntities(getByDocument(document), null, value, limit);

  }

  default Flux<BaleenEntity> getByDocumentAndType(final BaleenDocument document, final String type,
      final String value, final int limit) {
    return filterEntities(getByDocument(document), type, value, limit);
  }

  Flux<BaleenEntity> getAll(final int offset, final int limit);

  default Flux<BaleenEntity> getAll(final int limit) {
    return getAll(0, limit);

  }

  default Flux<BaleenEntity> getByType(final String type, final int limit) {
    return filterEntities(getAll(limit), type, null, limit);
  }

  default Flux<BaleenEntity> getByValue(final String value, final int limit) {
    return filterEntities(getAll(limit), null, value, limit);

  }

  default Flux<BaleenEntity> getByTypeAndValue(final String type, final String value,
      final int limit) {
    return filterEntities(getAll(limit), type, value, limit);
  }

  default Mono<BaleenEntity> mentionEntity(final BaleenMention mention) {
    return getById(mention.getEntityId());
  }

  static Flux<BaleenEntity> filterEntities(final Flux<BaleenEntity> in, final String type,
      final String value, final int limit) {
    Flux<BaleenEntity> flux = in;
    if (type != null) {
      flux = flux.filterWhen(e -> e.getTypes().any(type::equals));
    }
    if (value != null) {
      flux = flux.filterWhen(e -> e.getValues().any(value::equals));
    }
    if (limit > 0) {
      flux = flux.take(limit);
    }
    return flux;
  }

  @Override
  default String getProviderType() {
    return "EntityProvider";
  }

  Mono<Long> count();

  Flux<TermBin> countByType();

}
