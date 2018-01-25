
package io.committed.ketos.plugins.data.mongo.providers;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.support.data.mongo.AbstractMongoDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentSearch;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentFilter.DocumentInfoFilter;
import io.committed.ketos.common.graphql.input.DocumentProbe;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.plugins.data.mongo.dao.MongoDocument;
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
  public DocumentSearchResult search(final BaleenDocumentSearch documentSearch, final int offset, final int size) {
    // TODO: apply filter(s) / vs aggregation if have entity/relations

    final Query query = createQuery(documentSearch);

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
    // TODO Apply the filter, if present

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
    final List<AggregationOperation> aggregations = new ArrayList<>();

    if (documentFilter.isPresent()) {
      final List<CriteriaDefinition> criteria = createQuery(documentFilter.get());
      criteria.forEach(Aggregation::match);
    }

    aggregations.add(group(field).count().as("count"));
    aggregations.add(project("count").and("_id").as("term"));
    final Aggregation aggregation = newAggregation(aggregations);

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

  private List<CriteriaDefinition> createQuery(final DocumentFilter documentFilter) {
    final List<CriteriaDefinition> list = new LinkedList<>();

    Criteria criteria = new Criteria();

    if (documentFilter.getInfo() != null) {
      final DocumentInfoFilter info = documentFilter.getInfo();
      if (info.getCaveats() != null) {
        criteria = criteria.and("document.caveats").in(info.getCaveats());
      }

      if (info.getReleasability() != null) {
        criteria = criteria.and("document.releasability").in(info.getReleasability());
      }

      if (info.getEndTimestamp() != null) {
        criteria = criteria.and("document.timestamp").lte(info.getEndTimestamp());
      }

      if (info.getStartTimestamp() != null) {
        criteria = criteria.and("document.timestamp").gte(info.getStartTimestamp());
      }

      if (info.getLanguage() != null) {
        criteria = criteria.and("document.language").is(info.getLanguage());
      }

      if (info.getSource() != null) {
        criteria = criteria.and("document.source").is(info.getSource());
      }

      if (info.getType() != null) {
        criteria = criteria.and("document.type").is(info.getType());
      }
    }

    if (documentFilter.getMetadata() != null) {
      for (final Map.Entry<String, Object> e : documentFilter.getMetadata().entrySet()) {
        criteria = criteria.and("metadata." + e.getKey()).is(e.getValue());
      }
    }

    // TOOD other stuff: publishedIds
    if (documentFilter.getPublishedIds() != null) {
      criteria = criteria.and("publishedIds").in(documentFilter.getPublishedIds());
    }

    list.add(criteria);

    if (documentFilter.getContent() != null) {
      list.add(TextCriteria.forDefaultLanguage().matching(documentFilter.getContent()));
    }


    return list;
  }

  private Query createQuery(final BaleenDocumentSearch request) {
    final Query query = new Query();

    if (request.getDocumentFilter() != null) {
      createQuery(request.getDocumentFilter()).forEach(query::addCriteria);
    }

    // TODO: Entity and relations... they would require an aggregation which wouldn't be a query here so
    // not here!

    return query;
  }

}

