package io.committed.ketos.data.elasticsearch.providers;

import java.util.List;
import java.util.Optional;
import org.elasticsearch.index.query.QueryBuilder;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.invest.support.data.utils.FieldUtils;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.OutputMention;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.intermediate.MentionSearchResult;
import io.committed.ketos.common.graphql.output.MentionSearch;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.data.elasticsearch.filters.MentionFilters;
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
    return getService().getByDocumentId(document.getId()).map(Converters::toBaleenMention);
  }

  @Override
  public Mono<BaleenMention> getById(final String id) {
    return getService().getByExternalId(id).map(Converters::toBaleenMention);
  }

  @Override
  public Flux<BaleenMention> getAll(final int offset, final int limit) {
    return getService().getAll(offset, limit).map(Converters::toBaleenMention);

  }

  @Override
  public Flux<TermBin> countByField(final Optional<MentionFilter> filter, final List<String> path,
      final int size) {
    final Optional<QueryBuilder> query = MentionFilters.toMentionsQuery(filter, "");
    final String field = FieldUtils.joinField(path);
    return getService().nestedTermAggregation(query, BaleenProperties.PROPERTIES, field, size);

  }

  @Override
  public MentionSearchResult search(final MentionSearch search, final int offset, final int limit) {

    final Optional<QueryBuilder> query = MentionFilters.toQuery(search);

    final Flux<BaleenMention> results;

    // TODO: count can be calculated

    if (query.isPresent()) {
      results = getService().search(query.get(), offset, limit)
          .map(Converters::toBaleenMention);
    } else {
      results = getAll(offset, limit);
    }

    return new MentionSearchResult(
        results,
        Mono.empty());

  }

  @Override
  public Mono<Long> count() {
    return getService().count();

  }

}
