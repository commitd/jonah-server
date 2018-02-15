package io.committed.ketos.plugins.data.mongo.providers;

import java.util.List;
import java.util.Optional;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.ketos.common.baleenconsumer.Converters;
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

public class MongoRelationProvider extends AbstractBaleenMongoDataProvider<OutputRelation>
    implements RelationProvider {

  @Autowired
  public MongoRelationProvider(final String dataset, final String datasource,
      final MongoDatabase mongoDatabase, final String collectionName) {
    super(dataset, datasource, mongoDatabase, collectionName, OutputRelation.class);
  }

  // @Override
  // public Flux<BaleenRelation> getAll(final int offset, final int limit) {
  // return toRelations(relations.findAll().skip(offset).take(limit));
  // }
  //
  // public Flux<BaleenRelation> getByDocument(final String id) {
  // return toRelations(relations.findByDocId(id));
  // }
  //
  // @Override
  // public Flux<BaleenRelation> getRelations(final BaleenDocument document) {
  // return getByDocument(document.getId());
  // }
  //
  // @Override
  // public Flux<BaleenRelation> getSourceRelations(final BaleenMention mention) {
  // return toRelations(relations.findBySource(mention.getId()));
  // }
  //
  // @Override
  // public Flux<BaleenRelation> getTargetRelations(final BaleenMention mention) {
  // return toRelations(relations.findByTarget(mention.getId()));
  // }
  //
  // @Override
  // public Mono<BaleenRelation> getById(final String id) {
  // return relations.findByExternalId(id).map(MongoRelation::toRelation);
  // }
  //
  // private Flux<BaleenRelation> toRelations(final Flux<MongoRelation> stream) {
  // return stream.map(MongoRelation::toRelation);
  // }
  //
  // @Override
  // public Mono<Long> count() {
  // return relations.count();
  // }
  //
  //
  // @Override
  // public Flux<TermBin> countByField(final Optional<RelationFilter> filter, final List<String> path,
  // final int limit) {
  // final String field = FieldUtils.joinField(path);
  // final Aggregation aggregation =
  // CriteriaUtils.createAggregation(
  // filter.map(RelationFilters::createCriteria),
  // group(field).count().as("count"),
  // project("count").and("_id").as("term"));
  //
  // return getTemplate().aggregate(aggregation, MongoDocument.class, TermBin.class);
  // }
  //
  // @Override
  // public RelationSearchResult search(final RelationSearch search, final int offset, final int
  // limit) {
  // Flux<BaleenRelation> results;
  // if (search.getRelationFilter() != null) {
  // final Criteria criteria = RelationFilters.createCriteria(search.getRelationFilter());
  // results = getTemplate().find(new Query(criteria), MongoRelation.class)
  // .skip(offset)
  // .take(limit)
  // .map(MongoRelation::toRelation);
  //
  // } else {
  // results = getAll(offset, limit);
  // }
  //
  // return new RelationSearchResult(results, Mono.empty());
  // }

  @Override
  public Mono<BaleenRelation> getById(final String id) {
    return findByExternalId(id).map(Converters::toBaleenRelation);
  }

  @Override
  public Flux<BaleenRelation> getByDocument(final BaleenDocument document) {
    return findByDocumentId(document.getId()).map(Converters::toBaleenRelation);
  }


  @Override
  public Flux<BaleenRelation> getAll(final int offset, final int size) {
    return findAll(offset, size).map(Converters::toBaleenRelation);
  }

  @Override
  public Flux<TermBin> countByField(final Optional<RelationFilter> filter, final List<String> path,
      final int size) {
    return termAggregation(RelationFilters.createFilter(filter), path, size);
  }

  @Override
  public RelationSearchResult search(final RelationSearch relationSearch, final int offset, final int size) {

    final Optional<Bson> filter = RelationFilters.createFilter(relationSearch);

    final Mono<Long> total;
    final Flux<BaleenRelation> flux;
    if (filter.isPresent()) {
      total = toMono(getCollection().count(filter.get()));
      flux = toFlux(getCollection().find(filter.get()))
          .skip(offset)
          .take(size)
          .map(Converters::toBaleenRelation);
    } else {
      total = count();
      flux = getAll(offset, size);
    }

    return new RelationSearchResult(flux, total);
  }

  @Override
  public Flux<BaleenRelation> getSourceRelations(final BaleenMention mention) {
    return findAllByField("source.externalId", mention.getId()).map(Converters::toBaleenRelation);
  }

  @Override
  public Flux<BaleenRelation> getTargetRelations(final BaleenMention mention) {
    return findAllByField("target.externalId", mention.getId()).map(Converters::toBaleenRelation);
  }
}
