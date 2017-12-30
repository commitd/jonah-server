
package io.committed.ketos.plugins.data.mongo.providers;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

import java.time.LocalDate;
import java.time.ZoneOffset;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.support.data.mongo.AbstractMongoDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.plugins.data.mongo.dao.MongoDocument;
import io.committed.ketos.plugins.data.mongo.repository.BaleenDocumentRepository;
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
  public Flux<BaleenDocument> search(final String search, final int offset, final int limit) {
    return documents
        .searchDocuments(search)
        // TODO: CF: I don't know how the implementation of Mongo Reactive works, but I assume
        // providing offset and limit upfront in the query would be more efficient.
        .skip(offset)
        .take(limit)
        .map(MongoDocument::toDocument);
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
  public Mono<Long> countSearchMatches(final String query) {
    return documents.countSearchDocuments(query);
  }

  @Override
  public Flux<TermBin> countByType() {
    return termAggregation("document.type");
  }


  @Override
  public Flux<TermBin> countByLanguage() {
    return termAggregation("document.language");

  }

  @Override
  public Flux<TermBin> countByClassification() {
    return termAggregation("document.classification");
  }

  @Override
  public Flux<TimeBin> countByDate() {
    final Aggregation aggregation = newAggregation(
        project().and("document.timestamp").dateAsFormattedString("%Y-%m-%d").as("date"),
        group("date").count().as("count"),
        project("count").and("_id").as("term"));
    return getTemplate().aggregate(aggregation, MongoDocument.class, TermBin.class)
        .map(t -> {
          final LocalDate date = LocalDate.parse(t.getTerm());
          return new TimeBin(date.atStartOfDay(ZoneOffset.UTC).toInstant(), t.getCount());
        });
  }

  protected Flux<TermBin> termAggregation(final String field) {
    final Aggregation aggregation = newAggregation(
        group(field).count().as("count"),
        project("count").and("_id").as("term"));
    return getTemplate().aggregate(aggregation, MongoDocument.class, TermBin.class);
  }



}

