package io.committed.ketos.data.elasticsearch.providers;

import java.util.List;
import java.util.Optional;
import org.elasticsearch.index.query.QueryBuilder;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.OutputEntity;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.intermediate.EntitySearchResult;
import io.committed.ketos.common.graphql.output.EntitySearch;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.data.elasticsearch.filters.EntityFilters;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchEntityProvider
    extends AbstractElasticsearchServiceDataProvider<OutputEntity, EsEntityService>
    implements EntityProvider {

  public ElasticsearchEntityProvider(final String dataset, final String datasource,
      final EsEntityService service) {
    super(dataset, datasource, service);
  }


  @Override
  public Mono<BaleenEntity> getById(final String id) {
    return getService().getByExternalId(id).map(Converters::toBaleenEntity);
  }

  @Override
  public Flux<BaleenEntity> getByDocument(final BaleenDocument document) {
    return getService().getByDocumentId(document.getId()).map(Converters::toBaleenEntity);
  }

  @Override
  public Flux<TermBin> countByField(final Optional<EntityFilter> filter, final List<String> path,
      final int limit) {
    return getService().termAggregation(EntityFilters.toQuery(filter, ""), path, limit);
  }

  @Override
  public Flux<BaleenEntity> getAll(final int offset, final int limit) {
    return getService().getAll(offset, limit).map(Converters::toBaleenEntity);

  }

  @Override
  public EntitySearchResult search(final EntitySearch entitySearch, final int offset, final int limit) {
    final Optional<QueryBuilder> query = EntityFilters.toQuery(entitySearch);

    if (query.isPresent()) {
      final Flux<OutputEntity> search =
          getService().search(query.get(), offset, limit);
      return new EntitySearchResult(search.map(Converters::toBaleenEntity), Mono.empty(), offset, limit);
    } else {
      return new EntitySearchResult(getAll(offset, limit), count(), offset, limit);
    }
  }

  @Override
  public Mono<Long> count() {
    return getService().count();
  }


}
