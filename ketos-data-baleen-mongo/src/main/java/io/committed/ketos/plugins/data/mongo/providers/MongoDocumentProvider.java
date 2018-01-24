
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
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.support.data.mongo.AbstractMongoDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentSearch;
import io.committed.ketos.common.graphql.input.DocumentFilter;
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
    // TODO: apply filter(s)

    final String query = documentSearch.getDocumentFilter().getContent();

    final Mono<Long> total = documents.countSearchDocuments(query);

    final Flux<BaleenDocument> flux = documents.searchDocuments(query)
        // TODO: CF: I don't know how the implementation of Mongo Reactive works, but I assume
        // providing offset and limit upfront in the query would be more efficient.
        .skip(offset)
        .take(size)
        .map(MongoDocument::toDocument);

    return new DocumentSearchResult(flux, total);
  }


  @Override
  public Flux<BaleenDocument> all(final int offset, final int size) {
    return documents.findAll()
        // CF: Move this offset&size... into the pagination
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

    return termAggregation(field);
  }

  protected Flux<TermBin> termAggregation(final String field) {
    final Aggregation aggregation =
        newAggregation(group(field).count().as("count"), project("count").and("_id").as("term"));
    return getTemplate().aggregate(aggregation, MongoDocument.class, TermBin.class);
  }

  @Override
  public Flux<BaleenDocument> getByExample(final DocumentProbe probe) {
    // TODO: Might need to review other matchers here
    final ExampleMatcher matcher = ExampleMatcher.matching()
        .withMatcher("content", match -> match.contains());
    return documents.findAll(Example.of(new MongoDocument(probe), matcher))
        .map(MongoDocument::toDocument);
  }



}

