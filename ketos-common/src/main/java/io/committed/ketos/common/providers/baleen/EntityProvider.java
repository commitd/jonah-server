package io.committed.ketos.common.providers.baleen;

import java.util.List;
import java.util.Optional;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.input.EntityProbe;
import io.committed.ketos.common.graphql.intermediate.EntitySearchResult;
import io.committed.ketos.common.graphql.output.EntitySearch;
import io.leangen.graphql.annotations.GraphQLContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EntityProvider extends DataProvider {
  Mono<BaleenEntity> getById(final String id);

  Flux<BaleenEntity> getByDocument(@GraphQLContext final BaleenDocument document);

  Flux<TermBin> countByField(Optional<EntityFilter> filter, List<String> path, final int limit);

  Flux<BaleenEntity> getAll(final int offset, final int limit);

  Flux<BaleenEntity> getByExample(final EntityProbe probe, final int offset, final int limit);


  EntitySearchResult search(final EntitySearch entitySearch,
      final int offset,
      final int limit);

  default Flux<BaleenEntity> getByDocument(@GraphQLContext final BaleenDocument document,
      final int offset,
      final int limit) {
    final EntityProbe probe = new EntityProbe();
    probe.setDocId(document.getId());
    return getByExample(probe, offset, limit);
  }

  Mono<Long> count();


  // TODO: delete me


  // default Flux<BaleenEntity> getByDocumentAndType(final BaleenDocument document, final String type,
  // final int limit) {
  // return filterEntities(getByDocument(document), type, null, limit);
  // }
  //
  // default Flux<BaleenEntity> getByDocumentAndValue(final BaleenDocument document,
  // final String value, final int limit) {
  // return filterEntities(getByDocument(document), null, value, limit);
  //
  // }
  //
  // default Flux<BaleenEntity> getByDocumentAndType(final BaleenDocument document, final String type,
  // final String value, final int limit) {
  // return filterEntities(getByDocument(document), type, value, limit);
  // }



  // default Flux<BaleenEntity> getByType(final String type, final int limit) {
  // return filterEntities(getAll(limit), type, null, limit);
  // }
  //
  // default Flux<BaleenEntity> getByValue(final String value, final int limit) {
  // return filterEntities(getAll(limit), null, value, limit);
  //
  // }

  // default Flux<BaleenEntity> getByTypeAndValue(final String type, final String value,
  // final int limit) {
  // return filterEntities(getAll(limit), type, value, limit);
  // }

  default Mono<BaleenEntity> mentionEntity(final BaleenMention mention) {
    return getById(mention.getEntityId());
  }


  @Override
  default String getProviderType() {
    return "EntityProvider";
  }


  // Flux<TermBin> countByType();

}
