package io.committed.ketos.plugins.data.mongo.providers;

import java.util.List;
import java.util.Optional;

import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.mongodb.reactivestreams.client.MongoDatabase;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.OutputMention;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.intermediate.MentionSearchResult;
import io.committed.ketos.common.graphql.output.MentionSearch;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.plugins.data.mongo.filters.MentionFilters;

/** Mongo MentionProvider. */
public class MongoMentionProvider extends AbstractBaleenMongoDataProvider<OutputMention>
    implements MentionProvider {

  @Autowired
  public MongoMentionProvider(
      final String dataset,
      final String datasource,
      final MongoDatabase mongoDatabase,
      final String collection) {
    super(dataset, datasource, mongoDatabase, collection, OutputMention.class);
  }

  @Override
  public Mono<BaleenMention> getById(final String id) {
    return findByExternalId(id).map(Converters::toBaleenMention);
  }

  @Override
  public Flux<BaleenMention> getByDocument(final BaleenDocument document) {
    return findByDocumentId(document.getId()).map(Converters::toBaleenMention);
  }

  @Override
  public Flux<BaleenMention> getAll(final int offset, final int size) {
    return findAll(offset, size).map(Converters::toBaleenMention);
  }

  @Override
  public Flux<TermBin> countByField(
      final Optional<MentionFilter> filter, final List<String> path, final int size) {
    return termAggregation(MentionFilters.createFilter(filter, "", false), path, size);
  }

  @Override
  public MentionSearchResult search(
      final MentionSearch mentionSearch, final int offset, final int size) {

    final Optional<Bson> filter = MentionFilters.createFilter(mentionSearch);

    final Mono<Long> total;
    final Flux<BaleenMention> flux;
    if (filter.isPresent()) {
      total = toMono(getCollection().count(filter.get()));
      flux =
          toFlux(getCollection().find(filter.get()))
              .skip(offset)
              .take(size)
              .map(Converters::toBaleenMention);
    } else {
      total = count();
      flux = getAll(offset, size);
    }

    return new MentionSearchResult(flux, total, offset, size);
  }
}
