package io.committed.ketos.plugins.data.mongo.providers;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.mongo.AbstractMongoDataProvider;
import io.committed.invest.support.data.utils.CriteriaUtils;
import io.committed.invest.support.data.utils.ExampleUtils;
import io.committed.invest.support.data.utils.FieldUtils;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.input.EntityProbe;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.intermediate.EntitySearchResult;
import io.committed.ketos.common.graphql.output.EntitySearch;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.plugins.data.mongo.dao.FakeMongoEntities;
import io.committed.ketos.plugins.data.mongo.dao.MongoEntities;
import io.committed.ketos.plugins.data.mongo.filters.EntityFilters;
import io.committed.ketos.plugins.data.mongo.filters.MentionFilters;
import io.committed.ketos.plugins.data.mongo.repository.BaleenEntitiesRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoEntityProvider extends AbstractMongoDataProvider implements EntityProvider {

  private final BaleenEntitiesRepository entities;

  @Autowired
  public MongoEntityProvider(final String dataset, final String datasource,
      final ReactiveMongoTemplate mongoTemplate, final BaleenEntitiesRepository entities) {
    super(dataset, datasource, mongoTemplate);
    this.entities = entities;
  }

  @Override
  public Mono<BaleenEntity> getById(final String id) {
    return entities.findById(id).map(MongoEntities::toEntity);
  }

  @Override
  public Flux<BaleenEntity> getByDocument(final BaleenDocument document) {
    return entities.findByDocId(document.getId()).map(MongoEntities::toEntity);
  }

  @Override
  public Mono<Long> count() {
    return entities.count();
  }

  @Override
  public Flux<BaleenEntity> getAll(final int offset, final int limit) {
    return entities.findAll().skip(offset).take(limit).map(MongoEntities::toEntity);
  }

  @Override
  public Flux<TermBin> countByField(final Optional<EntityFilter> filter, final List<String> path, final int limit) {
    final String field = FieldUtils.joinField(path);
    return termAggregation(filter, field).take(limit);
  }

  protected Flux<TermBin> termAggregation(final Optional<EntityFilter> filter, final String field) {

    // TODO: unwind entities here... (ie mentions)... so I think that's count per mention... should
    // group by entityId and field (setting a value to 1 to say that this had entity had that field)
    // then group by count for the field?
    // Not clear... ie what we should be counting here.

    final Aggregation aggregation =
        CriteriaUtils.createAggregation(
            filter.map(EntityFilters::createCriteria),
            unwind("entities"),
            group(field).count().as("count"),
            project("count").and("_id").as("term"));


    // TODO: FIXME HEre I use FakeMongoEntities which enables Spring to know that the type existings
    // inside entities (by knowing that entities is a list of BaleenMention)
    // however in the rest I use MongoEntities since that allows us to grab anything in the Baleen
    // properties. Maybe there's a hybrid (exist BaleenMention from Document)...
    return getTemplate().aggregate(aggregation, FakeMongoEntities.class, TermBin.class);
  }

  @Override
  public Flux<BaleenEntity> getByExample(final EntityProbe probe, final int offset, final int limit) {
    final ExampleMatcher matcher = ExampleUtils.classlessMatcher();
    return entities.findAll(Example.of(new MongoEntities(probe), matcher))
        .skip(offset).take(limit)
        .map(MongoEntities::toEntity);
  }

  @Override
  public EntitySearchResult search(final EntitySearch entitySearch, final int offset, final int limit) {

    final List<CriteriaDefinition> criteria = new LinkedList<>();

    if (entitySearch.getEntityFilter() != null) {
      criteria.add(EntityFilters.createCriteria(entitySearch.getEntityFilter()));
    }

    if (entitySearch.getMentionFilters() != null) {
      for (final MentionFilter f : entitySearch.getMentionFilters()) {
        criteria.add(MentionFilters.createCriteria(f, "", "entities"));
      }
    }

    final Query query = CriteriaUtils.createQuery(criteria);
    final Mono<Long> total = getTemplate().count(query, MongoEntities.class);
    final Flux<BaleenEntity> flux = getTemplate().find(query, MongoEntities.class)
        .skip(offset)
        .take(limit)
        .map(MongoEntities::toEntity);

    return new EntitySearchResult(flux, total);
  }
}
