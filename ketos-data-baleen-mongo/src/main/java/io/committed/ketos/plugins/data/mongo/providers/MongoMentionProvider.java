package io.committed.ketos.plugins.data.mongo.providers;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.NonFieldExposingReplaceRootOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.mongo.AbstractMongoDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.intermediate.MentionSearchResult;
import io.committed.ketos.common.graphql.output.MentionSearch;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.plugins.data.mongo.dao.MongoEntities;
import io.committed.ketos.plugins.data.mongo.dao.MongoMention;
import io.committed.ketos.plugins.data.mongo.data.CountOutcome;
import io.committed.ketos.plugins.data.mongo.filters.MentionFilters;
import io.committed.ketos.plugins.data.mongo.repository.BaleenEntitiesRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoMentionProvider extends AbstractMongoDataProvider implements MentionProvider {

  private final BaleenEntitiesRepository entities;

  @Autowired
  public MongoMentionProvider(final String dataset, final String datasource,
      final ReactiveMongoTemplate mongoTemplate, final BaleenEntitiesRepository entities) {
    super(dataset, datasource, mongoTemplate);

    this.entities = entities;
  }

  @Override
  public Flux<BaleenMention> getByDocument(final BaleenDocument document) {
    return getMentionsByDocumentId(document.getId());
  }

  @Override
  public Mono<BaleenMention> source(final BaleenRelation relation) {
    return relationMentionById(relation, relation.getSourceId());
  }

  @Override
  public Mono<BaleenMention> target(final BaleenRelation relation) {
    return relationMentionById(relation, relation.getTargetId());
  }

  private Flux<BaleenEntity> getByDocumentId(final String id) {
    return entities.findByDocId(id).map(MongoEntities::toEntity);
  }

  private Flux<BaleenMention> getMentionsByDocumentId(final String documentId) {
    return getByDocumentId(documentId)
        .flatMap(BaleenEntity::getMentions);
  }

  private Mono<BaleenMention> relationMentionById(final BaleenRelation relation,
      final String sourceId) {
    return getMentionsByDocumentId(relation.getDocId())
        .filter(m -> sourceId.equals(m.getId()))
        .next();
  }

  @Override
  public Mono<BaleenMention> getById(final String id) {
    return aggregateOverMentions(MongoMention.class,
        Aggregation.match(Criteria.where("externalId").is(id)))
            .next()
            .map(MongoMention::toMention);
  }

  @Override
  public Flux<BaleenMention> getAll(final int offset, final int limit) {
    return aggregateOverMentions(MongoMention.class)
        .skip(offset)
        .take(limit)
        .map(MongoMention::toMention);
  }


  @Override
  public Mono<Long> count() {
    return aggregateOverMentions(CountOutcome.class,
        Aggregation.count().as("total"))
            .next()
            .map(CountOutcome::getTotal);
  }

  @Override
  public Flux<TermBin> countByField(final Optional<MentionFilter> filter, final List<String> path, final int limit) {

    // There's no nesting mention properties (sadly!)... so the field is just the last path segment
    final String field = path.get(path.size() - 1);

    return aggregateOverMentions(TermBin.class,
        filter.map(f -> Aggregation.match(MentionFilters.createCriteria(filter.get(), "", ""))).orElse(null),
        group(field).count().as("count"),
        Aggregation.project("count").and("_id").as("term"));
  }

  @Override
  public MentionSearchResult search(final MentionSearch search, final int offset, final int limit) {

    Flux<BaleenMention> results;
    if (search.getMentionFilter() != null) {
      final Criteria criteria = MentionFilters.createCriteria(search.getMentionFilter(), "", "");
      results = aggregateOverMentions(Document.class,
          Aggregation.match(criteria))
              .map(d -> new MongoMention(d))
              .map(MongoMention::toMention);
    } else {
      results = getAll(offset, limit);
    }

    return new MentionSearchResult(results, Mono.empty());
  }



  private <T> Flux<T> aggregateOverMentions(final Class<T> clazz, final AggregationOperation... operations) {
    final List<AggregationOperation> list = extractMention();
    Arrays.stream(operations).filter(Objects::nonNull).forEach(list::add);
    final Aggregation aggregation = Aggregation.newAggregation(list);
    return getTemplate().aggregate(aggregation, MongoEntities.class, clazz);
  }


  /**
   * Convert the entities collection into a mentions-collections-like via aggregation
   *
   * @return
   */
  private List<AggregationOperation> extractMention() {
    final List<AggregationOperation> operations = new LinkedList<>();

    final Map<String, Object> map = new HashMap<>();
    map.put("entities.entityId", "$_id");
    map.put("entities.docId", "$docId");
    map.put("entities._id", "$entities.externalId");

    operations.add(Aggregation.unwind("entities"));
    operations.add(new AddFieldsOperation(map));
    // Ideally this would be: operations.add(Aggregation.replaceRoot("entities"));
    // See NonFieldExposingReplaceRootOperation for reasons why it.
    operations.add(new NonFieldExposingReplaceRootOperation("entities"));

    return operations;
  }
}
