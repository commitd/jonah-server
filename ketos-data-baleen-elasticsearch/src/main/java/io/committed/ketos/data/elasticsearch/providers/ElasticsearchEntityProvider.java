package io.committed.ketos.data.elasticsearch.providers;

import java.util.List;
import java.util.Optional;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.intermediate.EntitySearchResult;
import io.committed.ketos.common.graphql.output.EntitySearch;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.data.elasticsearch.dao.EsMention;
import io.committed.ketos.data.elasticsearch.repository.EsMentionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public class ElasticsearchEntityProvider
    extends AbstractElasticsearchDataProvider
    implements EntityProvider {

  private final EsMentionService service;

  public ElasticsearchEntityProvider(final String dataset, final String datasource,
      final EsMentionService service) {
    super(dataset, datasource);
    this.service = service;
  }


  @Override
  public Mono<BaleenEntity> getById(final String id) {
    return service.getById(id).map(EsMention::toBaleenEntity);
  }

  @Override
  public Flux<BaleenEntity> getByDocument(final BaleenDocument document) {
    return service.getByDocument(document.getId()).map(EsMention::toBaleenEntity);
  }

  @Override
  public Flux<TermBin> countByField(final Optional<EntityFilter> filter, final List<String> path, final int limit) {
    // AS mention id = entity id... we can just make this into a mention filter
    return service.countByField(toMentionFilter(filter), path, limit);
  }

  @Override
  public Flux<BaleenEntity> getAll(final int offset, final int limit) {
    return service.getAll(offset, limit).map(EsMention::toBaleenEntity);

  }

  @Override
  public EntitySearchResult search(final EntitySearch entitySearch, final int offset, final int limit) {
    final Optional<MentionFilter> mentionFilter = toMentionFilter(Optional.ofNullable(entitySearch.getEntityFilter()));
    final Flux<EsMention> search = service.search(mentionFilter, entitySearch.getMentionFilters(), offset, limit);
    return new EntitySearchResult(search.map(EsMention::toBaleenEntity), Mono.empty());
  }

  @Override
  public Mono<Long> count() {
    return service.count();
  }

  private Optional<MentionFilter> toMentionFilter(final Optional<EntityFilter> filter) {
    return filter.map(ef -> {
      final MentionFilter mf = new MentionFilter();
      mf.setDocId(ef.getDocId());
      mf.setId(ef.getId());
      return mf;
    });
  }

}
