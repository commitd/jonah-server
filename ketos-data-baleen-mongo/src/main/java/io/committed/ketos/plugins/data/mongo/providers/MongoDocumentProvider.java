
package io.committed.ketos.plugins.data.mongo.providers;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.support.data.mongo.AbstractMongoDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentSearch;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentProbe;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.plugins.data.mongo.BooleanOperator;
import io.committed.ketos.plugins.data.mongo.dao.MongoDocument;
import io.committed.ketos.plugins.data.mongo.filters.DocumentFilters;
import io.committed.ketos.plugins.data.mongo.filters.MentionFilters;
import io.committed.ketos.plugins.data.mongo.filters.RelationFilters;
import io.committed.ketos.plugins.data.mongo.repository.BaleenDocumentRepository;
import io.committed.ketos.plugins.data.mongo.utils.CriteriaUtils;
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
  public DocumentSearchResult search(final BaleenDocumentSearch documentSearch, final int offset, final int size) {
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

    final List<AggregationOperation> operations = new ArrayList<>();

    // Add the document matches
    DocumentFilters.createCriteria(documentFilter).stream().map(Aggregation::match).forEach(operations::add);

    if (!mentionFilters.isEmpty()) {
      // NOTE: This pipeline puts all the entities into the document and then all the relations. THere is
      // a chance this will cause hit the 16mn document limit of Mongo.

      // Join entities to document
      // NOTE: This has a chance of going over the 16mb
      operations.add(Aggregation.lookup("entities", "externalId", "docId", "entities"));


      // Execute that match
      final List<Criteria> list = MentionFilters.createCriteria(mentionFilters, "entities.");
      operations.add(Aggregation.match(CriteriaUtils.combineCriteria(list, BooleanOperator.AND)));

      // Drop the entities, we don't need them any more
      operations.add(Aggregation.project().andExclude("entities"));
    }


    if (relationFilters.isEmpty()) {
      // NOTE: This pipeline puts all the relations into the document. THere is
      // a chance this will cause hit the 16mn document limit of Mongo.

      // Join entities to document
      // NOTE: This has a chance of going over the 16mb
      operations.add(Aggregation.lookup("full_relations", "externalId", "docId", "relations"));


      // Execute that match
      final List<Criteria> list = RelationFilters.createCriteria(relationFilters, "relations.");
      operations.add(Aggregation.match(CriteriaUtils.combineCriteria(list, BooleanOperator.AND)));

      // Drop the entities, we don't need them any more
      operations.add(Aggregation.project().andExclude("relations"));
    }


    final Aggregation aggregation = newAggregation(operations);
    final Flux<MongoDocument> documents =
        getTemplate().aggregate(aggregation, MongoDocument.class, MongoDocument.class);

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
  public Flux<BaleenDocument> all(final int offset, final int size) {
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
  public Flux<TermBin> countByField(final Optional<DocumentFilter> documentFilter, final List<String> path) {
    if (path.size() < 2) {
      return Flux.empty();
    }

    // Copy the list and modify to match out naming...
    final List<String> mongoPath = new ArrayList<>(path);
    if ("info".equals(mongoPath.get(0))) {
      mongoPath.set(0, "document");
    }

    final String field = MongoUtils.joinField(mongoPath);

    return termAggregation(documentFilter, field);
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
    final ExampleMatcher matcher = MongoUtils.exampleMatcher()
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

