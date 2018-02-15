package io.committed.ketos.plugins.data.mongo.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import io.committed.invest.core.constants.BooleanOperator;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentFilter.DocumentInfoFilter;
import io.committed.ketos.common.graphql.output.DocumentSearch;

public final class DocumentFilters {

  private static final String QUERY_PREFIX = "q";


  private DocumentFilters() {
    // Singleton
  }

  public static Optional<Bson> createFilter(final Optional<DocumentFilter> documentFilter) {
    return documentFilter.flatMap(DocumentFilters::createFilter);
  }

  public static Optional<Bson> createFilter(final DocumentFilter documentFilter) {

    if (documentFilter == null) {
      return Optional.empty();
    }

    final List<Bson> filters = new LinkedList<>();

    // Text search must be first (in aggregations)
    if (documentFilter.getContent() != null) {
      filters.add(Filters.text(documentFilter.getContent()));
    }


    if (documentFilter.getInfo() != null) {
      final DocumentInfoFilter info = documentFilter.getInfo();
      if (info.getCaveats() != null) {
        filters.add(Filters.in("properties.caveats", info.getCaveats()));
      }

      if (info.getReleasability() != null) {
        filters.add(Filters.in("properties.releasability", info.getCaveats()));
      }

      if (info.getEndTimestamp() != null) {
        filters.add(Filters.lte("properties.timestamp", info.getEndTimestamp()));
      }

      if (info.getStartTimestamp() != null) {
        filters.add(Filters.gte("properties.timestamp", info.getStartTimestamp()));
      }

      if (info.getLanguage() != null) {
        filters.add(Filters.eq("properties.language", info.getLanguage()));
      }

      if (info.getSource() != null) {
        filters.add(Filters.eq("properties.source", info.getSource()));
      }

      if (info.getType() != null) {
        filters.add(Filters.eq("properties.type", info.getType()));
      }

      if (info.getPublishedId() != null) {
        filters.add(Filters.in("properties.publishedIds.id", info.getPublishedId()));
      }

    }

    if (documentFilter.getMetadata() != null) {
      for (final Map.Entry<String, Object> e : documentFilter.getMetadata().entrySet()) {
        filters.add(Filters.elemMatch("metadata", Filters.and(
            Filters.eq("metadata.key", e.getKey()),
            Filters.eq("metadata.value", e.getValue()))));

      }
    }

    if (documentFilter.getProperties() != null) {
      for (final Map.Entry<String, Object> e : documentFilter.getProperties().entrySet()) {
        filters.add(Filters.eq("propertiers." + e.getKey(), e.getValue()));
      }
    }


    if (filters.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(Filters.and(filters));
  }

  public static boolean isAggregation(final DocumentSearch documentSearch) {
    return documentSearch.hasMentions() || documentSearch.hasRelations();
  }

  public static Optional<Bson> createFilter(final DocumentSearch documentSearch) {
    // This ignores the relation / mention

    return createFilter(Optional.ofNullable(documentSearch.getDocumentFilter()));
  }

  public static List<Bson> createAggregation(final DocumentSearch documentSearch, final String documentCollection,
      final String relationCollection, final String mentionCollection) {

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

    // You will definitely want to have an index or docId in relation, mention and externalId in
    // documents!
    // this will improve performance by order of magnitude or more.

    // @formatter:off

    /* In Mongo this looks like:...

      db.documents.aggregate(
    // Document search
    { "$match" : { "$text" : { "$search" : "test" } } },
    // Just retain the document id
     { $project : { "externalId": 1} },
//     // Join on the entities
    { "$lookup" : {
        "from" : "entities",
        localField: "externalId",
        foreignField: "docId",
        "as": "entities"
      }
    },
//     // Unwind to mentions (we need to need to do this in order to make the queries work in $cond
    { $unwind: {path: "$entities" }},
    { $replaceRoot: {newRoot: "$entities" }},
//     // For each query run it and if it matches 'flag the match'
    {
        $addFields: {
            q1: { $cond: [ {$and: [{$eq: ["$type", "Temporal"]}]}, true, false] },
            q2: { $cond: [{"type": "Buzzword"}, true, false] }
        }
      },
     { $group: { _id: "$docId", q1:{ $max: '$q1'}, q2:{ $max: '$q2'}  } },
 //      // And query:
     { $match: { $and: [{'q1':true}, {'q2':true}]} },
// //    Or query:  { $match: { $or: [{'q1':true}, {'q2':true}]} },
     { $project: { "externalId": "$_id" }},
     // Relations
      { "$lookup" : {
        "from" : "relations",
        localField: "externalId",
        foreignField: "docId",
        "as": "relations"
       }
    },
    { $unwind: {path: "$relations" }},
    { $replaceRoot: {newRoot: "$relations" }},
    {
        $addFields: {
            q1: { $cond: [ {$and: [{$eq: ["$source.type", "Buzzword"]}]}, true, false] },
            q2: { $cond: [{"type": "sentenceRelation"}, true, false] }
        }
      },
     { $group: { _id: "$docId", q1:{ $max: '$q1'}, q2:{ $max: '$q2'}  } },
//      // And vs Or:
     { $match: { $and: [{'q1':true}, {'q2':true}]} },
//
//     // Find the document again:
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


    final List<Bson> pipeline = new ArrayList<>();

    // Add the document matches
    DocumentFilters.createFilter(documentSearch.getDocumentFilter()).ifPresent(f -> {
      pipeline.add(Aggregates.match(f));
    });

    // Just keep the doc id:
    pipeline.add(Aggregates.project(Projections.include(BaleenProperties.EXTERNAL_ID)));


    if (!documentSearch.hasMentions()) {
      final List<Bson> filters = documentSearch.getMentionFilters().stream()
          .map(MentionFilters::createFilter)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toList());
      addFiltersToAggregation(pipeline, filters, mentionCollection);
    }


    if (!documentSearch.hasRelations()) {
      final List<Bson> filters = documentSearch.getRelationFilters().stream()
          .map(RelationFilters::createFilter)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toList());
      addFiltersToAggregation(pipeline, filters, relationCollection);
    }

    // Now join the document on again

    addJoinToDocuments(pipeline, documentCollection);

    return pipeline;
  }

  private static void addJoinToDocuments(final List<Bson> pipeline, final String documentCollection) {
    final String joinedField = "joined_document";

    pipeline.add(
        Aggregates.lookup(documentCollection, BaleenProperties.EXTERNAL_ID, BaleenProperties.EXTERNAL_ID,
            joinedField));
    pipeline.add(Aggregates.unwind(joinedField));
    pipeline.add(Aggregates.replaceRoot(joinedField));
  }

  private static void addFiltersToAggregation(final List<Bson> pipeline,
      final List<Bson> filters, final String collectionToJoin) {

    // Join relations (lookup and unwind)
    final String joinedField = "joined";
    pipeline
        .add(Aggregates.lookup(collectionToJoin, BaleenProperties.EXTERNAL_ID, BaleenProperties.DOC_ID, joinedField));
    pipeline.add(Aggregates.unwind(joinedField));
    // Move into the relations 'field' as our root (so that relationFilter works)
    pipeline.add(Aggregates.replaceRoot(joinedField));


    // Create cond for each query
    final int numFilters = filters.size();

    final List<Field<?>> filterConditional = new ArrayList<>(numFilters);
    for (int i = 0; i < numFilters; i++) {
      final Bson f = filters.get(i);
      final String key = QUERY_PREFIX + i;

      final Document cond = new Document("$cond", Arrays.asList(f, true, false));
      filterConditional.add(new Field<Document>(key, cond));
    }
    pipeline.add(Aggregates.addFields(filterConditional));

    // Group back to document level
    final List<BsonField> groupByFields = new ArrayList<>(numFilters);
    for (int i = 0; i < numFilters; i++) {
      final String key = QUERY_PREFIX + i;
      groupByFields.add(Accumulators.max(key, key));
    }
    pipeline.add(Aggregates.group(BaleenProperties.DOC_ID, groupByFields));

    // Apply the and/or filter
    final BooleanOperator operator = BooleanOperator.AND;
    final List<Bson> queryMatched = new ArrayList<>(numFilters);
    for (int i = 0; i < numFilters; i++) {
      final String key = QUERY_PREFIX + i;
      queryMatched.add(Filters.eq(key, true));
    }

    // TODO: if operator is OR / AND
    pipeline.add(Aggregates.match(Filters.and(queryMatched)));

    // Project back to being jsut like document with externalId
    pipeline.add(Aggregates.project(Projections.computed("externalId", "_id")));
  }

}


