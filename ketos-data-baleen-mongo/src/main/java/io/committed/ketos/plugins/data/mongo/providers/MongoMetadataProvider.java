
package io.committed.ketos.plugins.data.mongo.providers;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.mongo.AbstractMongoDataProvider;
import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.plugins.data.mongo.dao.MongoDocument;
import io.committed.ketos.plugins.data.mongo.repository.BaleenDocumentRepository;
import reactor.core.publisher.Flux;

public class MongoMetadataProvider extends AbstractMongoDataProvider implements MetadataProvider {

  private final BaleenDocumentRepository documents;

  public MongoMetadataProvider(final String dataset, final String datasource,
      final ReactiveMongoTemplate mongoTemplate, final BaleenDocumentRepository documents) {
    super(dataset, datasource, mongoTemplate);
    this.documents = documents;
  }

  @Override
  public Flux<TermBin> countByKey() {
    return countByKey(null);
  }


  @Override
  public Flux<TermBin> countByKey(final String key) {
    // db.documents.aggregate([
    // {$project: {m: {$objectToArray: "$metadata"}}},
    // {$group: {_id:"$m.k" , count:{ $sum: 1}}},
    // {$project: {term: "$_id", count: "$count"}}
    // ])

    final List<AggregationOperation> list = new ArrayList<>();

    if (key != null) {
      list.add(
          match(Criteria.where(String.format("metadata.%s", key)).exists(true)));
    }

    list.addAll(Arrays.asList(
        project().and(objectToArray("metadata")).as("m"),
        unwind("m"),
        group("m.k").count().as("count"),
        project("count").and("_id").as("term")));

    final Aggregation aggregation = newAggregation(list);

    return getTemplate().aggregate(aggregation, MongoDocument.class, TermBin.class);
  }

  @Override
  public Flux<TermBin> countByValue() {
    return countByValue(null);
  }

  @Override
  public Flux<TermBin> countByValue(final String key) {

    // db.documents.aggregate([
    // {$project: {m: {$objectToArray: "$metadata"}}},
    // {$project: {v: "$m.v"}},
    // {$unwind: "$v"},
    // {$unwind: "$v"},
    // {$group: {_id:"$v" , count:{ $sum: 1}}},
    // {$project: {term: "$_id", count: "$count"}}
    // ])


    final List<AggregationOperation> list = new ArrayList<>();

    if (key != null) {
      list.add(
          match(Criteria.where(String.format("metadata.%s", key)).exists(true)));
    }

    list.addAll(Arrays.asList(
        project().and(objectToArray("metadata")).as("m"),
        project().and("m.v").as("v"),
        unwind("v"),
        unwind("v"),
        group("v").count().as("count"),
        project("count").and("_id").as("term")));

    final Aggregation aggregation = newAggregation(list);


    return getTemplate().aggregate(aggregation, MongoDocument.class, TermBin.class);
  }

}

