package io.committed.ketos.data.elasticsearch.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.metrics.sum.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.elasticsearch.utils.SourceUtils;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import io.committed.ketos.data.elasticsearch.dao.EsMention;
import io.committed.ketos.data.elasticsearch.filters.MentionFilters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EsMentionService {

  private final EsDocumentService documents;
  private final ObjectMapper mapper;

  public EsMentionService(final EsDocumentService documents, final ObjectMapper mapper) {
    this.documents = documents;
    this.mapper = mapper;
  }

  public Mono<EsMention> getById(final String id) {
    // As we are going to unwind this... we need to use a nested - top hits aggregation

    final NativeSearchQueryBuilder query = documents.queryBuilder()
        .addAggregation(AggregationBuilders.nested("agg", EsDocument.MENTIONS)
            .subAggregation(AggregationBuilders.topHits("hits")
                .fetchSource(true)
                .size(1)))
        .withQuery(QueryBuilders.nestedQuery(EsDocument.MENTIONS,
            QueryBuilders.termQuery(EsDocument.MENTIONS_PREFIX + "externalId", id), ScoreMode.None));

    return documents.query(query, response -> {
      final Nested nested = response.getAggregations().get("agg");

      final TopHits hits = nested.getAggregations().get("hits");
      final SearchHit[] searchHits = hits.getHits().getHits();

      if (searchHits.length > 0) {
        return SourceUtils.convertSource(mapper, searchHits[0].getSourceAsString(), EsMention.class);
      } else {
        return Mono.empty();
      }
    });
  }

  public Flux<EsMention> getAll(final int offset, final int limit) {
    return findMentions(Optional.empty(), offset, limit);
  }

  public Mono<Long> count() {
    // We need a script here in order to calculate the answer...
    // TODO: ideally we'd change the consumer to produce the number as part of the output!

    final NativeSearchQueryBuilder qb = documents.queryBuilder()
        .addAggregation(
            new SumAggregationBuilder("agg").script(new Script("doc." + EsDocument.MENTIONS + ".length")));

    return documents.query(qb, response -> {
      final ParsedSum sum = response.getAggregations().get("agg");
      return Mono.just((long) sum.getValue());
    });
  }

  public Flux<EsMention> search(final Optional<MentionFilter> mustMentionFilter,
      final Collection<MentionFilter> additionalMentionFilters,
      final int offset, final int limit) {

    // Here we and everything together. However in future additionalMentions bmight be configurable
    // between AND / OR.
    // mustMention would always be and.

    final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

    mustMentionFilter.flatMap(f -> MentionFilters.toQuery(f, EsDocument.MENTIONS_PREFIX)).ifPresent(boolQuery::must);

    additionalMentionFilters.stream()
        .map(f -> MentionFilters.toQuery(f, EsDocument.MENTIONS_PREFIX))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(boolQuery::must);

    return findMentions(Optional.ofNullable(boolQuery), offset, limit);
  }


  public Flux<TermBin> countByField(final Optional<MentionFilter> filter, final List<String> path, final int limit) {

    final Optional<QueryBuilder> query = MentionFilters.toQuery(filter, EsDocument.MENTIONS_PREFIX);

    final String field = path.get(path.size() - 1);

    // TODO: I think this is the wrong count. It's the number of documents which have that
    // rather than the number of relations? Might need a nested aggrgation?

    return documents.termAggregation(query, field, limit);
  }

  public Flux<EsMention> getByDocument(final String id) {
    return documents.getById(id).flatMapMany(d -> Flux.fromIterable(d.getEntities()));
  }

  private Flux<EsMention> findMentions(final Optional<QueryBuilder> query, final int offset, final int limit) {
    // We get all the documents which have a mentions from 0 to offset + limit
    // that way we know we have at least limit number of mentions.


    final QueryBuilder hasMentions = QueryBuilders.existsQuery(EsDocument.MENTIONS);
    final QueryBuilder qb;

    if (query.isPresent()) {
      qb = QueryBuilders.boolQuery().must(hasMentions).must(query.get());
    } else {
      qb = hasMentions;
    }

    return documents.search(qb, 0, offset + limit)
        .flatMap(d -> Flux.fromIterable(d.getEntities()))
        .skip(offset)
        .take(limit);
  }
}
