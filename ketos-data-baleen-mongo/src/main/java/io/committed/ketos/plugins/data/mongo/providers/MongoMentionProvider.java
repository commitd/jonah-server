// package io.committed.ketos.plugins.data.mongo.providers;
//
// import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.LinkedList;
// import java.util.List;
// import java.util.Map;
// import java.util.Objects;
// import java.util.Optional;
// import org.bson.Document;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
// import org.springframework.data.mongodb.core.aggregation.Aggregation;
// import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
// import org.springframework.data.mongodb.core.aggregation.MatchOperation;
// import org.springframework.data.mongodb.core.aggregation.NonFieldExposingReplaceRootOperation;
// import com.mongodb.reactivestreams.client.MongoDatabase;
// import io.committed.invest.core.dto.analytic.TermBin;
// import io.committed.invest.support.data.mongo.AbstractMongoCollectionDataProvider;
// import io.committed.ketos.common.baleenconsumer.OutputMention;
// import io.committed.ketos.common.data.BaleenDocument;
// import io.committed.ketos.common.data.BaleenEntity;
// import io.committed.ketos.common.data.BaleenMention;
// import io.committed.ketos.common.data.BaleenRelation;
// import io.committed.ketos.common.graphql.input.MentionFilter;
// import io.committed.ketos.common.graphql.intermediate.MentionSearchResult;
// import io.committed.ketos.common.graphql.output.MentionSearch;
// import io.committed.ketos.common.providers.baleen.MentionProvider;
// import io.committed.ketos.plugins.data.mongo.data.CountOutcome;
// import io.committed.ketos.plugins.data.mongo.filters.MentionFilters;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
//
// public class MongoMentionProvider extends AbstractMongoCollectionDataProvider<OutputMention>
// implements MentionProvider {
//
// @Autowired
// public MongoMentionProvider(final String dataset, final String datasource,
// final MongoDatabase mongoDatabase, final String collection) {
// super(dataset, datasource, mongoDatabase, collection, OutputMention.class);
// }
//
// @Override
// public Flux<BaleenMention> getByDocument(final BaleenDocument document) {
// return getMentionsByDocumentId(document.getId());
// }
//
// private Flux<BaleenEntity> getByDocumentId(final String id) {
// return entities.findByDocId(id).map(MongoEntities::toEntity);
// }
//
// private Flux<BaleenMention> getMentionsByDocumentId(final String documentId) {
// return getByDocumentId(documentId);;
// }
//
// private Mono<BaleenMention> relationMentionById(final BaleenRelation relation,
// final String sourceId) {
// return getMentionsByDocumentId(relation.getDocId())
// .filter(m -> sourceId.equals(m.getId()))
// .next();
// }
//
// @Override
// public Mono<BaleenMention> getById(final String id) {
// final MentionFilter filter = new MentionFilter();
// filter.setId(id);
// final MentionSearch search = MentionSearch.builder().mentionFilter(filter).build();
// return search(search, 0, 1).getResults().next();
// }
//
// @Override
// public Flux<BaleenMention> getAll(final int offset, final int limit) {
// return search(MentionSearch.builder().build(), offset, limit).getResults();
// }
//
//
// @Override
// public Mono<Long> count() {
// return aggregateOverMentions(CountOutcome.class, Optional.empty(),
// Aggregation.count().as("total"))
// .next()
// .map(CountOutcome::getTotal);
// }
//
// @Override
// public Flux<TermBin> countByField(final Optional<MentionFilter> filter, final List<String> path,
// final int limit) {
//
// // There's no nesting mention properties (sadly!)... so the field is just the last path segment
// final String field = path.get(path.size() - 1);
//
// return aggregateOverMentions(TermBin.class,
// filter,
// group(field).count().as("count"),
// Aggregation.project("count").and("_id").as("term"));
// }
//
// @Override
// public MentionSearchResult search(final MentionSearch search, final int offset, final int limit)
// {
//
// final Flux<BaleenMention> results = aggregateOverMentions(Document.class,
// Optional.ofNullable(search.getMentionFilter()))
// .skip(offset)
// .take(limit)
// .map(MongoMention::new)
// .map(MongoMention::toMention);
//
// return new MentionSearchResult(results, Mono.empty());
// }
//
//
//
// private <T> Flux<T> aggregateOverMentions(final Class<T> clazz, final Optional<MentionFilter>
// filter,
// final AggregationOperation... operations) {
// final List<AggregationOperation> list = new LinkedList<>();
//
// // WE apply the same filter as a pre and post match... which is a bit strange but
// // - pre match will filter down the entities
// // - post match will filter down the mentions
//
// final Optional<MatchOperation> preAggregation =
// filter.map(
// f -> Aggregation.match(MentionFilters.createCriteria(filter.get(), "",
// MongoEntities.MENTIONS_PREFIX)));
// final Optional<MatchOperation> postAggregation =
// filter.map(f -> Aggregation.match(MentionFilters.createCriteria(filter.get(), "", "")));
//
//
// preAggregation.ifPresent(list::add);
// extractMention(list);
// Arrays.stream(operations).filter(Objects::nonNull).forEach(list::add);
// postAggregation.ifPresent(list::add);
//
//
// final Aggregation aggregation = Aggregation.newAggregation(list);
// return getTemplate().aggregate(aggregation, "entities", clazz);
// }
//
//
// /**
// * Convert the entities collection into a mentions-collections-like via aggregation
// *
// * @return
// */
// private void extractMention(final List<AggregationOperation> operations) {
//
// final Map<String, Object> map = new HashMap<>();
// map.put("entities.entityId", "$_id");
// map.put("entities.docId", "$docId");
// map.put("entities._id", "$entities.externalId");
//
// operations.add(Aggregation.unwind("entities"));
// operations.add(new AddFieldsOperation(map));
// // Ideally this would be: operations.add(Aggregation.replaceRoot("entities"));
// // See NonFieldExposingReplaceRootOperation for reasons why it.
// operations.add(new NonFieldExposingReplaceRootOperation("entities"));
//
// }
// }
