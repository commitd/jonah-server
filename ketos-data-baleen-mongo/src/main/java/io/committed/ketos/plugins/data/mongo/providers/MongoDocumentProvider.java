
package io.committed.ketos.plugins.data.mongo.providers;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Cond;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import io.committed.invest.core.constants.BooleanOperator;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.support.data.mongo.AbstractMongoDataProvider;
import io.committed.invest.support.data.utils.CriteriaUtils;
import io.committed.invest.support.data.utils.ExampleUtils;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentProbe;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import io.committed.ketos.common.graphql.output.DocumentSearch;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.plugins.data.mongo.dao.MongoDocument;
import io.committed.ketos.plugins.data.mongo.filters.DocumentFilters;
import io.committed.ketos.plugins.data.mongo.filters.MentionFilters;
import io.committed.ketos.plugins.data.mongo.filters.RelationFilters;
import io.committed.ketos.plugins.data.mongo.repository.BaleenDocumentRepository;
import io.committed.ketos.plugins.data.mongo.utils.MongoUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoDocumentProvider extends AbstractMongoDataProvider implements DocumentProvider {

  private final BaleenDocumentRepository documents;

  public MongoDocumentProvider(final String dataset, final String datasource,
      final ReactiveMongoTemplate mongoTemplate, final BaleenDocumentRepository documents) {
    super(dataset, datasource, mongoTemplate);
    this.documents = documents;
  }

  @Override
  public Mono<BaleenDocument> getById(final String id) {
    return documents.findByExternalId(id).map(MongoDocument::toDocument);
  }

  @Override
  public DocumentSearchResult search(final DocumentSearch documentSearch, final int offset, final int size) {
    if (documentSearch.hasMentions() || documentSearch.hasRelations()) {
      // IF we have relations / mentions we have to do an aggregation in order to do a join ($lookup).

      return search(documentSearch.getDocumentFilter(), documentSearch.getMentionFilters(),
          documentSearch.getRelationFilters(), offset, size);

    } else {
      // Otherwise life is easier and we can just query on the document

      return search(documentSearch.getDocumentFilter(), offset, size);
    }
  }

  private DocumentSearchResult search(final DocumentFilter documentFilter, final List<MentionFilter> mentionFilters,
      final List<RelationFilter> relationFilters, final int offset, final int size) {


    // In order to join in Mongo you need to do an aggregation, and then use $lookup.
    // Lookup as two opiotns one to so a simple join (id in one collection -> docId to another),
    // and one to do a join where you can preprocess (through a pipeline) the collection to join.
    // That would be great, because we could apply filtering to entities before join,
    // but is very slow (100x slower in our implementation)

    // If you use the Mongo aggregation id-id lookup to do: document filter, then join entities onto
    // document you create a Mongo Document which is easily over the 16 mb limit.
    // So that doesn't work at all, Mongo will just stop..

    // Instead we need to need to use the pipeline optimisation to do lookup-then-unwind which basically
    // will join the documents-entities and immediately spilt them again (so you have document-single
    // entity).
    // At that point you have something that will fit into 16mb (assuming the entity did originallu!)

    // So our approach here is:
    // - doucmentFilter
    // - extact just the docIds (the rest is useless for filtering with)
    // - join entities - via the lookup-unwind approach
    // - for each entity we've joined:
    // - since technically an entityFilter is a mentionFilter we actually need to unwind again... this
    // is a bit silly in that both times we have multiple copies of the same mention within an entity,
    // bu
    // - For each entityFilter: add a flag which says we amtched this query (named e1...eN for entities
    // and r1... rN for
    // relations)
    // -
    // - Group on document Id... such that we have e1: true, e2; false, where that is that docId has
    // mention which makes e1 but not e2.
    // - MAatch the boolean operator we want AND = e1 & e2 & .. , OR = e1 | e2
    // - So you are left with jsut the docId which have documents or entities which match

    // Repeat the above for relations - join on extenralId with lookup-unwind, etc

    // At this stage you have docId which match doc, entity and relation filters.

    // Rejoin document collection with docId, so we lookup documents again to basically join the full
    // documents back on.

    // @formatter:off

    /* In Mongo this looks like:...

      db.documents.aggregate(
    // Document search
    { "$match" : { "$text" : { "$search" : "Iraq" } } },
    { "$match" : { "document.type" : "re3d" } },
    // Just retain the document id
    { $project : { "externalId": 1} },
    // Join on the entities
    { "$lookup" : {
        "from" : "entities",
        localField: "externalId",
        foreignField: "docId",
        "as": "entities"
      }
    },
    // Unwind to mentions (we need to need to do this in order to make the queries work in $cond
    { $unwind: {path: "$entities" }},
    { $replaceRoot: {newRoot: "$entities" }},
    { $unwind: {path: "$entities" } },
    // For each query run it and if it matches 'flag the match'
    {
        $addFields: {
            q1: { $cond: [ {$and: [{$eq: ["$entities.type", "Temporal"]}]}, true, false] },
            q2: { $cond: [{"entities": {"type": "Buzzword"}}, true, false] }
        }
      },
     { $group: { _id: "$docId", q1:{ $max: '$q1'}, q2:{ $max: '$q2'}  } },
     // And query:
     { $match: { $and: [{'q1':true}, {'q2':true}]} },
//    Or query:  { $match: { $or: [{'q1':true}, {'q2':true}]} },
     { $project: { "externalId": "$_id" }},

    // Relations
      { "$lookup" : {
        "from" : "full_relations",
        localField: "externalId",
        foreignField: "docId",
        "as": "relations"
      }
    },
    { $unwind: {path: "$relations" }},
    { $replaceRoot: {newRoot: "$relations" }},
    {
        $addFields: {
            q1: { $cond: [ {$and: [{$eq: ["$value", "released"]}]}, true, false] },
            q2: { $cond: [{"sourceType": "Buzzword"}, true, false] }
        }
      },
     { $group: { _id: "$docId", q1:{ $max: '$q1'}, q2:{ $max: '$q2'}  } },
     // And vs Or:
     { $match: { $and: [{'q1':true}, {'q2':true}]} },

    // Find the document again:
    { "$lookup" : {
        "from" : "documents",
        localField: "_id",
        foreignField: "externalId",
        "as": "documents"
      }
    },
    { $unwind: {path: "$documents" }},
    { $replaceRoot: { newRoot: "$documents" } }

)
     */

 // @formatter:on


    final List<AggregationOperation> operations = new ArrayList<>();

    // Add the document matches
    DocumentFilters.createCriteria(documentFilter).stream().map(Aggregation::match).forEach(operations::add);

    // Just keep the id:
    operations.add(project("externalId"));

    final String QUERY_PREIX = "q";

    if (!mentionFilters.isEmpty()) {
      final int numFilters = mentionFilters.size();

      // Join on entities with a lookup then immediately unwind to use pipeline optimisation
      operations.add(Aggregation.lookup("entities", "externalId", "docId", "joined_entities"));
      operations.add(Aggregation.unwind("joined_entities"));
      // Move into the entities 'field' as our root (so that mentionFilter works)
      operations.add(Aggregation.replaceRoot("joined_entities"));
      // Unwind each entities to mention
      operations.add(Aggregation.unwind("entities"));


      // Entity filter
      final Map<String, Object> filterConditional = new HashMap<>();
      for (int i = 0; i < numFilters; i++) {
        final MentionFilter f = mentionFilters.get(i);
        final String key = QUERY_PREIX + i;

        final Cond cond = ConditionalOperators
            .when(MentionFilters.createCriteria(f))
            .then(true)
            .otherwise(false);

        filterConditional.put(key, cond);
      }
      operations.add(new AddFieldsOperation(filterConditional));

      // Group back to document level
      GroupOperation groupToDocument = Aggregation.group("docId");
      for (int i = 0; i < numFilters; i++) {
        final String key = QUERY_PREIX + i;
        groupToDocument = groupToDocument.max(key).as(key);
      }
      operations.add(groupToDocument);

      // Apply the and/or filter
      final BooleanOperator operator = BooleanOperator.AND;
      final List<Criteria> queryMatched = new ArrayList<>(numFilters);
      for (int i = 0; i < numFilters; i++) {
        final String key = QUERY_PREIX + i;
        queryMatched.add(Criteria.where(key).is(true));
      }
      operations.add(Aggregation.match(CriteriaUtils.combineCriteria(queryMatched, operator)));

      // Project back to being jsut like document with externalId
      operations.add(project().and("_id").as("externalId"));
    }


    if (!relationFilters.isEmpty()) {
      final int numFilters = relationFilters.size();

      // Join relations (lookup and unwind)
      operations.add(Aggregation.lookup("full_relations", "externalId", "docId", "joined_relations"));
      operations.add(Aggregation.unwind("joined_relations"));
      // Move into the relations 'field' as our root (so that relationFilter works)
      operations.add(Aggregation.replaceRoot("joined_relations"));

      // relation filter
      final Map<String, Object> filterConditional = new HashMap<>();
      for (int i = 0; i < numFilters; i++) {
        final RelationFilter f = relationFilters.get(i);
        final String key = QUERY_PREIX + i;

        final Cond cond = ConditionalOperators
            .when(RelationFilters.createCriteria(f))
            .then(true)
            .otherwise(false);

        filterConditional.put(key, cond);
      }
      operations.add(new AddFieldsOperation(filterConditional));

      // Group back to document level
      GroupOperation groupToDocument = Aggregation.group("docId");
      for (int i = 0; i < numFilters; i++) {
        final String key = QUERY_PREIX + i;
        groupToDocument = groupToDocument.max(key).as(key);
      }
      operations.add(groupToDocument);

      // Apply the and/or filter
      final BooleanOperator operator = BooleanOperator.AND;
      final List<Criteria> queryMatched = new ArrayList<>(numFilters);
      for (int i = 0; i < numFilters; i++) {
        final String key = QUERY_PREIX + i;
        queryMatched.add(Criteria.where(key).is(true));
      }
      operations.add(Aggregation.match(CriteriaUtils.combineCriteria(queryMatched, operator)));

      // Project back to being jsut like document with externalId
      operations.add(project().and("_id").as("externalId"));

    }


    // Now join the document on again

    operations.add(Aggregation.lookup("documents", "externalId", "externalId", "joined_document"));
    operations.add(Aggregation.unwind("joined_document"));
    operations.add(Aggregation.replaceRoot("joined_document"));


    // Finally actually do the aggregation!

    final Aggregation aggregation = newAggregation(operations);
    final Flux<MongoDocument> documents =
        getTemplate().aggregate(aggregation, MongoDocument.class, MongoDocument.class)
            .skip(offset)
            .take(size);

    // TODO: Count or is that asking too much (will require another aggregation)?

    return new DocumentSearchResult(documents.map(MongoDocument::toDocument),
        Mono.empty());
  }


  private DocumentSearchResult search(final DocumentFilter documentFilter, final int offset, final int size) {
    final Query query = DocumentFilters.createQuery(documentFilter);
    final Mono<Long> total = getTemplate().count(query, MongoDocument.class);
    final Flux<BaleenDocument> flux = getTemplate().find(query, MongoDocument.class)
        .skip(offset)
        .take(size)
        .map(MongoDocument::toDocument);

    return new DocumentSearchResult(flux, total);
  }

  @Override
  public Flux<BaleenDocument> getAll(final int offset, final int size) {
    return documents.findAll()
        .skip(offset)
        .take(size)
        .map(MongoDocument::toDocument);
  }

  @Override
  public Mono<Long> count() {
    return documents.count();
  }

  @Override
  public Flux<TimeBin> countByDate(final Optional<DocumentFilter> documentFilter) {
    final Aggregation aggregation = newAggregation(
        project().and("document.timestamp").dateAsFormattedString("%Y-%m-%d").as("date"),
        group("date").count().as("count"), project("count").and("_id").as("term"));
    return getTemplate().aggregate(aggregation, MongoDocument.class, TermBin.class).map(t -> {
      final LocalDate date = LocalDate.parse(t.getTerm());
      return new TimeBin(date.atStartOfDay(ZoneOffset.UTC).toInstant(), t.getCount());
    });
  }

  @Override
  public Flux<TermBin> countByField(final Optional<DocumentFilter> documentFilter, final List<String> path,
      final int size) {
    if (path.size() < 2) {
      return Flux.empty();
    }

    // Copy the list and modify to match out naming...
    final List<String> mongoPath = new ArrayList<>(path);
    if ("info".equals(mongoPath.get(0))) {
      mongoPath.set(0, "document");
    }

    final String field = MongoUtils.joinField(mongoPath);

    return termAggregation(documentFilter, field).take(size);
  }

  protected Flux<TermBin> termAggregation(final Optional<DocumentFilter> documentFilter, final String field) {
    final Aggregation aggregation =
        CriteriaUtils.createAggregation(DocumentFilters.createCriteria(documentFilter.orElse(null)),
            group(field).count().as("count"),
            project("count").and("_id").as("term"));

    return getTemplate().aggregate(aggregation, MongoDocument.class, TermBin.class);
  }


  @Override
  public Flux<BaleenDocument> getByExample(final DocumentProbe probe, final int offset, final int limit) {
    // TODO: Might need to review other matchers here
    final ExampleMatcher matcher = ExampleUtils.classlessMatcher()
        // NOTE: This uses a regex match under the hood, which may not make use of the $text index on the
        // content field (and hence be slower).
        .withMatcher("content", match -> match.contains());
    final Example<MongoDocument> example = Example.of(new MongoDocument(probe), matcher);
    return documents.findAll(example)
        .skip(offset)
        .take(limit)
        .map(MongoDocument::toDocument);
  }



}

