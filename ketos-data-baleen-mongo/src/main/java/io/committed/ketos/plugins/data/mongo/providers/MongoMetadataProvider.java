
package io.committed.ketos.plugins.data.mongo.providers;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

  public MongoMetadataProvider(final String dataset, final String datasource,
      final ReactiveMongoTemplate mongoTemplate, final BaleenDocumentRepository documents) {
    super(dataset, datasource, mongoTemplate);
  }

  @Override
  public Flux<TermBin> countByKey(final Optional<String> key, final int size) {
    return countByKey(key.orElse(null), size);

  }

  @Override
  public Flux<TermBin> countByValue(final Optional<String> key, final int size) {
    return countByValue(key.orElse(null), size);
  }

  private Flux<TermBin> countByKey(final String key, final int size) {
    // db.documents.aggregate([
    // {$project: {m: {$objectToArray: "$metadata"}}},
    // {$group: {_id:"$m.k" , count:{ $sum: 1}}},
    // {$project: {term: "$_id", count: "$count"}}
    // ])

    final List<AggregationOperation> list = new ArrayList<>();

    if (key != null) {
      list.add(match(Criteria.where(String.format("metadata.%s", key)).exists(true)));
    }

    list.addAll(Arrays.asList(project().and(objectToArray("metadata")).as("m"), unwind("m"),
        group("m.k").count().as("count"), project("count").and("_id").as("term")));

    final Aggregation aggregation = newAggregation(list);

    return getTemplate().aggregate(aggregation, MongoDocument.class, TermBin.class)
        .take(size);
  }

  private Flux<TermBin> countByValue(final String key, final int size) {

    // @formatter:off

    /**
    db.documents.aggregate(
       // If key { "$match" : { "metadata.Application-Version" : { "$exists" : true } } },
        { "$project" : { "m" : { "$objectToArray" : "$metadata" } } },
        { "$unwind" : "$m" },
        // if key { "$match" : { "m.k" : "Application-Version" } },
        { "$project" : { "v" : "$m.v" } },
        { "$unwind" : "$v" },
        { "$unwind" : "$v" },
        { "$group" : { "_id" : "$v", "count" : { "$sum" : 1 } } },
        { "$project" : { "count" : 1, "term" : "$_id" } }
        )
    */

 // @formatter:on


    final List<AggregationOperation> list = new ArrayList<>();

    if (key != null) {
      list.add(match(Criteria.where(String.format("metadata.%s", key)).exists(true)));
    }

    list.add(project().and(objectToArray("metadata")).as("m"));
    list.add(unwind("m"));

    if (key != null) {
      list.add(match(Criteria.where("m.k").is(key)));
    }


    list.addAll(Arrays.asList(
        project().and("m.v").as("v"),
        unwind("v"),
        unwind("v"),
        group("v").count().as("count"),
        project("count").and("_id").as("term")));

    final Aggregation aggregation = newAggregation(list);


    return getTemplate().aggregate(aggregation, MongoDocument.class, TermBin.class)
        .take(size);
  }

}

