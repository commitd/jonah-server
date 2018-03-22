package io.committed.ketos.plugins.data.mongo.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import io.committed.invest.support.mongo.utils.FilterUtils;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentFilter.DocumentInfoFilter;
import io.committed.ketos.common.graphql.output.DocumentSearch;
import io.committed.ketos.common.utils.ValueConversion;

public final class DocumentFilters {

  private static final String QUERY_PREFIX = "q";


  private DocumentFilters() {
    // Singleton
  }

  public static Optional<Bson> createFilter(final Optional<DocumentFilter> filter) {

    if (!filter.isPresent()) {
      return Optional.empty();
    }

    final DocumentFilter documentFilter = filter.get();

    final List<Bson> filters = new LinkedList<>();

    // Text search must be first (in aggregations)
    ValueConversion.stringValue(documentFilter.getContent())
        .map(Filters::text)
        .ifPresent(filters::add);

    ValueConversion.stringValue(documentFilter.getId())
        .map(s -> Filters.eq(BaleenProperties.EXTERNAL_ID, s))
        .ifPresent(filters::add);

    if (documentFilter.getInfo() != null) {
      final DocumentInfoFilter info = documentFilter.getInfo();

      ValueConversion.stringValue(info.getCaveats())
          .map(s -> Filters.in("properties.caveats", s))
          .ifPresent(filters::add);

      ValueConversion.stringValue(info.getReleasability())
          .map(s -> Filters.in("properties.releasability", s))
          .ifPresent(filters::add);

      if (info.getEndTimestamp() != null) {
        filters.add(Filters.lte("properties.timestamp", info.getEndTimestamp().getTime()));
      }

      if (info.getStartTimestamp() != null) {
        filters.add(Filters.gte("properties.timestamp", info.getStartTimestamp().getTime()));
      }

      ValueConversion.stringValue(info.getLanguage())
          .map(s -> Filters.eq("properties.language", s))
          .ifPresent(filters::add);

      ValueConversion.stringValue(info.getSource())
          .map(s -> Filters.eq("properties.source", s))
          .ifPresent(filters::add);

      ValueConversion.stringValue(info.getType())
          .map(s -> Filters.eq("properties.type", s))
          .ifPresent(filters::add);

      ValueConversion.stringValue(info.getPublishedId())
          .map(s -> Filters.in("properties.publishedIds.id", s))
          .ifPresent(filters::add);

      ValueConversion.stringValue(info.getClassification())
          .map(s -> Filters.in("properties.classification", s))
          .ifPresent(filters::add);
    }

    if (documentFilter.getMetadata() != null) {
      documentFilter.getMetadata().stream()
          .filter(p -> ValueConversion.isValueOrOther(p.getValue()))
          .map(e -> Filters.elemMatch("metadata", Filters.and(
              Filters.eq(BaleenProperties.METADATA + "." + BaleenProperties.METADATA_KEY, e.getKey()),
              Filters.eq(BaleenProperties.METADATA + "." + BaleenProperties.METADATA_VALUE,
                  ValueConversion.valueOrNull(e.getValue())))))
          .forEach(filters::add);

    }

    if (documentFilter.getProperties() != null) {
      documentFilter.getProperties().stream()
          .filter(p -> ValueConversion.isValueOrOther(p.getValue()))
          .map(e -> Filters.eq(BaleenProperties.PROPERTIES + "." + e.getKey(),
              ValueConversion.valueOrNull(e.getValue())))
          .forEach(filters::add);

    }


    return FilterUtils.combine(filters);
  }

  public static boolean isAggregation(final DocumentSearch documentSearch) {
    return documentSearch.hasMentions() || documentSearch.hasRelations() || documentSearch.hasEntities();
  }

  public static Optional<Bson> createFilter(final DocumentSearch documentSearch) {
    // This ignores the relation / mention

    return createFilter(Optional.ofNullable(documentSearch.getDocumentFilter()));
  }

  public static List<Bson> createAggregation(final DocumentSearch documentSearch, final String documentCollection,
      final String mentionCollection, final String entityCollection, final String relationCollection) {

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
    DocumentFilters.createFilter(Optional.ofNullable(documentSearch.getDocumentFilter())).ifPresent(f -> {
      pipeline.add(Aggregates.match(f));
    });

    // Just keep the doc id:
    pipeline.add(Aggregates.project(Projections.include(BaleenProperties.EXTERNAL_ID)));


    if (documentSearch.hasMentions()) {
      final List<Bson> filters =
          MentionFilters.createFilters(documentSearch.getMentionFilters().stream(), true)
              .collect(Collectors.toList());
      addFiltersToAggregation(pipeline, filters, mentionCollection);
    }

    if (documentSearch.hasEntities()) {
      final List<Bson> filters =
          EntityFilters.createFilters(documentSearch.getEntityFilters().stream(), true)
              .collect(Collectors.toList());
      addFiltersToAggregation(pipeline, filters, entityCollection);
    }

    if (documentSearch.hasRelations()) {
      final List<Bson> filters =
          RelationFilters.createFilters(documentSearch.getRelationFilters().stream(), true)
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
    pipeline.add(Aggregates.unwind("$" + joinedField));
    pipeline.add(Aggregates.replaceRoot("$" + joinedField));
  }

  private static void addFiltersToAggregation(final List<Bson> pipeline,
      final List<Bson> filters, final String collectionToJoin) {

    // Join relations (lookup and unwind)
    final String joinedField = "joined";
    pipeline
        .add(Aggregates.lookup(collectionToJoin, BaleenProperties.EXTERNAL_ID, BaleenProperties.DOC_ID, joinedField));
    pipeline.add(Aggregates.unwind("$" + joinedField));
    // Move into the relations 'field' as our root (so that relationFilter works)
    pipeline.add(Aggregates.replaceRoot("$" + joinedField));


    // Create cond for each query
    final int numFilters = filters.size();

    final List<Bson> filterConditional = new ArrayList<>(numFilters);
    for (int i = 0; i < numFilters; i++) {
      final Bson f = filters.get(i);
      final String key = QUERY_PREFIX + i;
      final Document cond = new Document("$cond", Arrays.asList(f, true, false));
      filterConditional.add(Projections.computed(key, cond));
    }
    filterConditional.add(Projections.include(BaleenProperties.DOC_ID));
    pipeline.add(Aggregates.project(
        Projections.fields(filterConditional)));

    // Group back to document level
    final List<BsonField> groupByFields = new ArrayList<>(numFilters);
    for (int i = 0; i < numFilters; i++) {
      final String key = QUERY_PREFIX + i;
      groupByFields.add(Accumulators.max(key, "$" + key));
    }
    pipeline.add(Aggregates.group("$" + BaleenProperties.DOC_ID, groupByFields));

    final List<Bson> queryMatched = new ArrayList<>(numFilters);
    for (int i = 0; i < numFilters; i++) {
      final String key = QUERY_PREFIX + i;
      queryMatched.add(Filters.eq(key, true));
    }

    // TODO: if operator is OR / AND
    pipeline.add(Aggregates.match(Filters.and(queryMatched)));

    // Project back to being jsut like document with externalId
    pipeline.add(Aggregates.project(Projections.computed("externalId", "$_id")));
  }

}


