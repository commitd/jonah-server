// package io.committed.ketos.data.elasticsearch.repository;
//
// import java.util.Collection;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import org.apache.lucene.search.join.ScoreMode;
// import org.elasticsearch.index.query.BoolQueryBuilder;
// import org.elasticsearch.index.query.InnerHitBuilder;
// import org.elasticsearch.index.query.QueryBuilder;
// import org.elasticsearch.index.query.QueryBuilders;
// import org.elasticsearch.search.SearchHit;
// import org.elasticsearch.search.SearchHits;
// import org.elasticsearch.search.aggregations.AggregationBuilders;
// import org.elasticsearch.search.aggregations.bucket.nested.Nested;
// import org.elasticsearch.search.aggregations.bucket.terms.Terms;
// import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
// import org.elasticsearch.search.aggregations.support.ValueType;
// import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import io.committed.invest.core.dto.analytic.TermBin;
// import io.committed.invest.support.elasticsearch.utils.SourceUtils;
// import io.committed.ketos.common.graphql.input.MentionFilter;
// import io.committed.ketos.data.elasticsearch.dao.EsDocument;
// import io.committed.ketos.data.elasticsearch.dao.EsMention;
// import io.committed.ketos.data.elasticsearch.filters.MentionFilters;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
//
// public class OldEsMentionService {
//
// private final EsDocumentService documents;
// private final ObjectMapper mapper;
//
// public OldEsMentionService(final EsDocumentService documents, final ObjectMapper mapper) {
// this.documents = documents;
// this.mapper = mapper;
// }
//
// public Mono<EsMention> getById(final String id) {
// return findMentions(Optional.empty(),
// Optional.of(QueryBuilders.termQuery(EsDocument.MENTIONS_PREFIX + "externalId", id)), 0, 1)
// .next();
// }
//
// public Flux<EsMention> getAll(final int offset, final int limit) {
// return findMentions(Optional.empty(), Optional.empty(), offset, limit);
// }
//
// public Mono<Long> count() {
// // We need a script here in order to calculate the answer...
// // https://discuss.elastic.co/t/count-number-of-array-element-for-each-document/17593/3
// // but it doesn't work... with our without the nested below...
//
// // final NativeSearchQueryBuilder qb = documents.queryBuilder()
// // .addAggregation(
// //
// // AggregationBuilders.nested("agg", EsDocument.MENTIONS)
// // .subAggregation(
// // AggregationBuilders.sum("sum").script(new Script("doc['" + EsDocument.MENTIONS +
// "'].length"))));
// //
// // return documents.query(qb, response -> {
// // final Nested nested = response.getAggregations().get("agg");
// // final Sum sum = nested.getAggregations().get("sum");
// // return Mono.just((long) sum.getValue());
// // });
//
// // TODO: ideally we'd change the consumer to produce the number as part of the output, then we
// could
// // just sum it.
//
// return Mono.empty();
// }
//
// public Flux<EsMention> search(final Optional<MentionFilter> mustMentionFilter,
// final Collection<MentionFilter> additionalMentionFilters,
// final int offset, final int limit) {
//
// // Here we and everything together. However in future additionalMentions bmight be configurable
// // between AND / OR.
// // mustMention would always be and.
//
// final BoolQueryBuilder documentQuery = QueryBuilders.boolQuery();
//
//
//
// mustMentionFilter.flatMap(f -> MentionFilters.toDocumentQuery(f))
// .ifPresent(documentQuery::must);
//
// additionalMentionFilters.stream()
// .map(f -> MentionFilters.toDocumentQuery(f))
// .filter(Optional::isPresent)
// .map(Optional::get)
// .forEach(documentQuery::must);
//
//
// final BoolQueryBuilder entitiesQuery = QueryBuilders.boolQuery();
//
//
// mustMentionFilter.flatMap(f -> MentionFilters.toMentionsQuery(f, EsDocument.MENTIONS_PREFIX))
// .ifPresent(entitiesQuery::must);
//
// additionalMentionFilters.stream()
// .map(f -> MentionFilters.toMentionsQuery(f, EsDocument.MENTIONS_PREFIX))
// .filter(Optional::isPresent)
// .map(Optional::get)
// .forEach(entitiesQuery::must);
//
// return findMentions(Optional.ofNullable(documentQuery), Optional.ofNullable(entitiesQuery),
// offset, limit);
// }
//
//
// public Flux<TermBin> countByField(final Optional<MentionFilter> filter, final List<String> path,
// final int limit) {
//
// // NOTE addition of .keyword so we can use the field data for aggegation
// final String field = path.get(path.size() - 1) + ".keyword";
//
// final NativeSearchQueryBuilder qb = documents.queryBuilder()
// .addAggregation(
// AggregationBuilders.nested("agg", EsDocument.MENTIONS)
// .subAggregation(new TermsAggregationBuilder("terms", ValueType.STRING)
// .field(EsDocument.MENTIONS_PREFIX + field).size(limit)));
//
// final Optional<QueryBuilder> documentQuery = MentionFilters.toDocumentQuery(filter);
// final Optional<QueryBuilder> mentionQuery = MentionFilters.toMentionsQuery(filter,
// EsDocument.MENTIONS_PREFIX);
//
// if (documentQuery.isPresent() || mentionQuery.isPresent()) {
// final BoolQueryBuilder query = QueryBuilders.boolQuery();
//
// documentQuery.ifPresent(q -> query.must(q));
//
// mentionQuery.ifPresent(q -> query.must(QueryBuilders.nestedQuery(EsDocument.MENTIONS,
// q, ScoreMode.None)));
//
//
// qb.withQuery(query);
// }
//
// return documents.query(qb, response -> {
// final Nested nested = response.getAggregations().get("agg");
// final Terms terms = nested.getAggregations().get("terms");
//
// return Flux.fromIterable(terms.getBuckets())
// .map(b -> new TermBin(b.getKeyAsString(), b.getDocCount()));
// });
// }
//
// public Flux<EsMention> getByDocument(final String id) {
// return documents.getById(id)
// .flatMapMany(d -> Flux.fromIterable(d.getEntities()))
// .doOnNext(m -> m.setDocumentId(id));
//
// }
//
// private Flux<EsMention> findMentions(final Optional<QueryBuilder> documentQuery,
// final Optional<QueryBuilder> entitiesQuery, final int mentionOffset,
// final int mentionLimit) {
// // We get all the documents which have a mentions from 0 to offset + limit
// // that way we know we have at least limit number of mentions.
//
//
// if (entitiesQuery.isPresent()) {
//
// final BoolQueryBuilder qb = QueryBuilders.boolQuery();
//
// documentQuery.ifPresent(qb::must);
//
// qb.must(QueryBuilders.nestedQuery(EsDocument.MENTIONS,
// entitiesQuery.get(), ScoreMode.None)
// .innerHit(new InnerHitBuilder().setSize(mentionLimit)));
//
//
// return documents.query(documents.queryBuilder()
// .withQuery(qb)
// .withPageable(org.springframework.data.domain.PageRequest.of(0, mentionOffset + mentionLimit)),
// response -> {
// final SearchHit[] searchHits = response.getHits().getHits();
// if (searchHits.length > 0) {
// return mapInnerSearchHitsToMentions(searchHits);
// } else {
// return Flux.empty();
// }
// });
// } else {
// // If we have no query.. then its just the 'exists'
//
// final BoolQueryBuilder qb = QueryBuilders.boolQuery()
// .must(QueryBuilders.nestedQuery(EsDocument.MENTIONS, QueryBuilders.matchAllQuery(),
// ScoreMode.None));
//
// documentQuery.ifPresent(qb::must);
//
// return documents
// .search(qb, 0,
// mentionOffset + mentionLimit)
// .flatMap(d -> Flux.fromIterable(d.getEntities())
// .doOnNext(m -> m.setDocumentId(d.getExternalId())))
// .skip(mentionOffset)
// .take(mentionLimit);
// }
//
// }
//
// private Flux<EsMention> mapInnerSearchHitsToMentions(final SearchHit[] searchHits) {
// return Flux.fromArray(searchHits)
// .flatMap(h -> {
// final Map<String, SearchHits> innerHitsMap = h.getInnerHits();
//
// if (innerHitsMap == null) {
// return Flux.empty();
// }
//
// final SearchHits innerHits = innerHitsMap.get(EsDocument.MENTIONS);
//
// if (innerHits == null) {
// return Flux.empty();
// }
//
// return SourceUtils.convertSource(mapper, h.getSourceAsString(), EsDocument.class)
// .flatMapMany(d -> {
// return Flux.fromArray(innerHits.getHits())
// .flatMap(i -> {
// return SourceUtils.convertSource(mapper, i.getSourceAsString(), EsMention.class)
// .doOnNext(m -> m.setDocumentId(d.getExternalId()));
// });
// });
// });
//
//
// }
//
// }
