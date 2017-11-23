
package io.committed.ketos.plugins.data.mongo.providers;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.plugins.data.mongo.dao.MongoDocument;
import io.committed.ketos.plugins.data.mongo.repository.BaleenDocumentRepository;
import io.committed.vessel.core.dto.analytic.TermBin;
import io.committed.vessel.server.data.providers.AbstractDataProvider;
import io.committed.vessel.server.data.providers.DatabaseConstants;
import reactor.core.publisher.Flux;

public class MongoMetadataProvider extends AbstractDataProvider implements MetadataProvider {

  private final BaleenDocumentRepository documents;
  private final ReactiveMongoTemplate mongoTemplate;

  public MongoMetadataProvider(final String dataset, final String datasource,
      final ReactiveMongoTemplate mongoTemplate, final BaleenDocumentRepository documents) {
    super(dataset, datasource);
    this.mongoTemplate = mongoTemplate;
    this.documents = documents;
  }


  @Override
  public String getDatabase() {
    return DatabaseConstants.MONGO;
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

    return mongoTemplate.aggregate(aggregation, MongoDocument.class, TermBin.class);
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


    return mongoTemplate.aggregate(aggregation, MongoDocument.class, TermBin.class);
  }

  // Spring doesn't have this yet
  private static AggregationExpression objectToArray(final String field) {
    return context -> new Document("$objectToArray", "$" + field);
  }
}

