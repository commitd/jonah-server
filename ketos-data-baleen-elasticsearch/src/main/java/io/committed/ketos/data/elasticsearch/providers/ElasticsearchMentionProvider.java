package io.committed.ketos.data.elasticsearch.providers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.OutputMention;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.intermediate.MentionSearchResult;
import io.committed.ketos.common.graphql.output.MentionSearch;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.data.elasticsearch.dao.EsMention;
import io.committed.ketos.data.elasticsearch.repository.EsMentionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchMentionProvider
    extends AbstractElasticsearchServiceDataProvider<OutputMention, EsMentionService>
    implements MentionProvider {

  public ElasticsearchMentionProvider(final String dataset, final String datasource,
      final EsMentionService service) {
    super(dataset, datasource, service);
  }

  @Override
  public Flux<BaleenMention> getByDocument(final BaleenDocument document) {
    return getService().getByDocument(document.getId()).map(Converters::toBaleenMention);
  }

  @Override
  public Mono<BaleenMention> getById(final String id) {
    return getService().getById(id).map(Converters::toBaleenMention);
  }

  @Override
  public Flux<BaleenMention> getAll(final int offset, final int limit) {
    return getService().getAll(offset, limit).map(Converters::toBaleenMention);

  }

  @Override
  public Flux<TermBin> countByField(final Optional<MentionFilter> filter, final List<String> path, final int limit) {
    return getService().countByField(filter, path, limit);

  }

  @Override
  public MentionSearchResult search(final MentionSearch search, final int offset, final int limit) {
    return new MentionSearchResult(
        getService().search(Optional.ofNullable(search.getMentionFilter()), Collections.emptyList(), offset, limit)
            .map(EsMention::toBaleenMention),
        Mono.empty());

  }

  @Override
  public Mono<Long> count() {
    return getService().count();

  }

}
