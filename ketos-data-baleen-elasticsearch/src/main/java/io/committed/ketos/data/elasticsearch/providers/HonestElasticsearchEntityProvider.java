package io.committed.ketos.data.elasticsearch.providers;

import java.util.List;
import java.util.Optional;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.input.EntityProbe;
import io.committed.ketos.common.graphql.intermediate.EntitySearchResult;
import io.committed.ketos.common.graphql.output.EntitySearch;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Elasticsearch does not have any real entities, just mentions!
// So in honesty you should return empty here for everything.

public class HonestElasticsearchEntityProvider
    extends AbstractElasticsearchServiceDataProvider<EsDocument, EsDocumentService>
    implements EntityProvider {

  public HonestElasticsearchEntityProvider(final String dataset, final String datasource,
      final EsDocumentService documentService) {
    super(dataset, datasource, documentService);
  }


  @Override
  public Mono<BaleenEntity> getById(final String id) {
    return Mono.empty();
  }

  @Override
  public Flux<BaleenEntity> getByDocument(final BaleenDocument document) {
    return Flux.empty();
  }

  @Override
  public Flux<TermBin> countByField(final Optional<EntityFilter> filter, final List<String> path, final int limit) {
    return Flux.empty();

  }

  @Override
  public Flux<BaleenEntity> getAll(final int offset, final int limit) {
    return Flux.empty();

  }

  @Override
  public Flux<BaleenEntity> getByExample(final EntityProbe probe, final int offset, final int limit) {
    return Flux.empty();

  }

  @Override
  public EntitySearchResult search(final EntitySearch entitySearch, final int offset, final int limit) {
    return new EntitySearchResult(Flux.empty(), Mono.empty());
  }

  @Override
  public Mono<Long> count() {
    return Mono.empty();
  }

}
