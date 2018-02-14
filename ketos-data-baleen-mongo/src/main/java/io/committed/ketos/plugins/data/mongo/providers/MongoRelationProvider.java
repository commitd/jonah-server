package io.committed.ketos.plugins.data.mongo.providers;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.mongo.AbstractMongoCollectionDataProvider;
import io.committed.invest.support.data.utils.CriteriaUtils;
import io.committed.invest.support.data.utils.FieldUtils;
import io.committed.ketos.common.baleenconsumer.OutputRelation;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.intermediate.RelationSearchResult;
import io.committed.ketos.common.graphql.output.RelationSearch;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.plugins.data.mongo.filters.RelationFilters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoRelationProvider extends AbstractMongoCollectionDataProvider<OutputRelation>
    implements RelationProvider {

  @Autowired
  public MongoRelationProvider(final String dataset, final String datasource,
      final MongoDatabase mongoDatabase, final String collectionName) {
    super(dataset, datasource, mongoDatabase, collectionName, OutputRelation.class);
  }

  @Override
  public Flux<BaleenRelation> getAll(final int offset, final int limit) {
    return toRelations(relations.findAll().skip(offset).take(limit));
  }

  public Flux<BaleenRelation> getByDocument(final String id) {
    return toRelations(relations.findByDocId(id));
  }

  @Override
  public Flux<BaleenRelation> getRelations(final BaleenDocument document) {
    return getByDocument(document.getId());
  }

  @Override
  public Flux<BaleenRelation> getSourceRelations(final BaleenMention mention) {
    return toRelations(relations.findBySource(mention.getId()));
  }

  @Override
  public Flux<BaleenRelation> getTargetRelations(final BaleenMention mention) {
    return toRelations(relations.findByTarget(mention.getId()));
  }

  @Override
  public Mono<BaleenRelation> getById(final String id) {
    return relations.findByExternalId(id).map(MongoRelation::toRelation);
  }

  private Flux<BaleenRelation> toRelations(final Flux<MongoRelation> stream) {
    return stream.map(MongoRelation::toRelation);
  }

  @Override
  public Mono<Long> count() {
    return relations.count();
  }


  @Override
  public Flux<TermBin> countByField(final Optional<RelationFilter> filter, final List<String> path, final int limit) {
    final String field = FieldUtils.joinField(path);
    final Aggregation aggregation =
        CriteriaUtils.createAggregation(
            filter.map(RelationFilters::createCriteria),
            group(field).count().as("count"),
            project("count").and("_id").as("term"));

    return getTemplate().aggregate(aggregation, MongoDocument.class, TermBin.class);
  }

  @Override
  public RelationSearchResult search(final RelationSearch search, final int offset, final int limit) {
    Flux<BaleenRelation> results;
    if (search.getRelationFilter() != null) {
      final Criteria criteria = RelationFilters.createCriteria(search.getRelationFilter());
      results = getTemplate().find(new Query(criteria), MongoRelation.class)
          .skip(offset)
          .take(limit)
          .map(MongoRelation::toRelation);

    } else {
      results = getAll(offset, limit);
    }

    return new RelationSearchResult(results, Mono.empty());
  }

}
