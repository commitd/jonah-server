package io.committed.ketos.data.elasticsearch.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValueType;
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
    return findMentions(Optional.of(QueryBuilders.termQuery(EsDocument.MENTIONS_PREFIX + "externalId", id)), 0, 1)
        .next();
  }

  public Flux<EsMention> getAll(final int offset, final int limit) {
    return findMentions(Optional.empty(), offset, limit);
  }

  public Mono<Long> count() {
    // We need a script here in order to calculate the answer...
    // https://discuss.elastic.co/t/count-number-of-array-element-for-each-document/17593/3
    // but it doesn't work... with our without the nested below...

    // final NativeSearchQueryBuilder qb = documents.queryBuilder()
    // .addAggregation(
    //
    // AggregationBuilders.nested("agg", EsDocument.MENTIONS)
    // .subAggregation(
    // AggregationBuilders.sum("sum").script(new Script("doc['" + EsDocument.MENTIONS + "'].length"))));
    //
    // return documents.query(qb, response -> {
    // final Nested nested = response.getAggregations().get("agg");
    // final Sum sum = nested.getAggregations().get("sum");
    // return Mono.just((long) sum.getValue());
    // });

    // TODO: ideally we'd change the consumer to produce the number as part of the output, then we could
    // just sum it.

    return Mono.empty();
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

    // NOTE addition of .keyword so we can use the field data for aggegation
    final String field = path.get(path.size() - 1) + ".keyword";

    final NativeSearchQueryBuilder qb = documents.queryBuilder()
        .addAggregation(
            AggregationBuilders.nested("agg", EsDocument.MENTIONS)
                .subAggregation(new TermsAggregationBuilder("terms", ValueType.STRING)
                    .field(EsDocument.MENTIONS_PREFIX + field).size(limit)));

    final Optional<QueryBuilder> query = MentionFilters.toQuery(filter, EsDocument.MENTIONS_PREFIX);

    if (query.isPresent()) {
      qb.withQuery(QueryBuilders.nestedQuery(EsDocument.MENTIONS,
          query.get(), ScoreMode.None));
    }

    return documents.query(qb, response -> {
      final Nested nested = response.getAggregations().get("agg");
      final Terms terms = nested.getAggregations().get("terms");

      return Flux.fromIterable(terms.getBuckets())
          .map(b -> new TermBin(b.getKeyAsString(), b.getDocCount()));
    });
  }

  public Flux<EsMention> getByDocument(final String id) {
    return documents.getById(id).flatMapMany(d -> Flux.fromIterable(d.getEntities()));
  }

  private Flux<EsMention> findMentions(final Optional<QueryBuilder> query, final int mentionOffset,
      final int mentionLimit) {
    // We get all the documents which have a mentions from 0 to offset + limit
    // that way we know we have at least limit number of mentions.



    if (query.isPresent()) {
      final QueryBuilder qb = QueryBuilders.nestedQuery(EsDocument.MENTIONS,
          query.get(), ScoreMode.None)
          .innerHit(new InnerHitBuilder().setSize(mentionLimit));


      return documents.query(documents.queryBuilder()
          .withQuery(qb)
          .withPageable(org.springframework.data.domain.PageRequest.of(0, mentionOffset + mentionLimit)), response -> {

            final SearchHit[] searchHits = response.getHits().getHits();

            if (searchHits.length > 0) {

              return Flux.fromArray(searchHits).flatMap(h -> {
                final Map<String, SearchHits> innerHits = h.getInnerHits();
                if (innerHits != null) {
                  final SearchHits inner = innerHits.get(EsDocument.MENTIONS);
                  return Flux.fromArray(inner.getHits());
                } else {
                  return Flux.empty();
                }
              }).flatMap(h -> {
                final String source = h.getSourceAsString();
                return SourceUtils.convertSource(mapper, source, EsMention.class);
              });
            } else {
              return Flux.empty();
            }
          });
    } else {
      // If we have no query.. then its just the exists
      return documents
          .search(QueryBuilders.nestedQuery(EsDocument.MENTIONS, QueryBuilders.matchAllQuery(), ScoreMode.None), 0,
              mentionOffset + mentionLimit)
          .flatMap(d -> Flux.fromIterable(d.getEntities()))
          .skip(mentionOffset)
          .take(mentionLimit);
    }

  }
}
