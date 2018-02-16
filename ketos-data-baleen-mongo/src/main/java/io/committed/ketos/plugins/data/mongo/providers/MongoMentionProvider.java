package io.committed.ketos.plugins.data.mongo.providers;

import java.util.List;
import java.util.Optional;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoMentionProvider extends AbstractBaleenMongoDataProvider<OutputMention>
    implements MentionProvider {

  @Autowired
  public MongoMentionProvider(final String dataset, final String datasource,
      final MongoDatabase mongoDatabase, final String collection) {
    super(dataset, datasource, mongoDatabase, collection, OutputMention.class);
  }

  // @Override
  // public Flux<BaleenMention> getByDocument(final BaleenDocument document) {
  // return getMentionsByDocumentId(document.getId());
  // }
  //
  // private Flux<BaleenEntity> getByDocumentId(final String id) {
  // return entities.findByDocId(id).map(MongoEntities::toEntity);
  // }
  //
  // private Flux<BaleenMention> getMentionsByDocumentId(final String documentId) {
  // return getByDocumentId(documentId);;
  // }
  //
  // private Mono<BaleenMention> relationMentionById(final BaleenRelation relation,
  // final String sourceId) {
  // return getMentionsByDocumentId(relation.getDocId())
  // .filter(m -> sourceId.equals(m.getId()))
  // .next();
  // }
  //
  // @Override
  // public Mono<BaleenMention> getById(final String id) {
  // final MentionFilter filter = new MentionFilter();
  // filter.setId(id);
  // final MentionSearch search = MentionSearch.builder().mentionFilter(filter).build();
  // return search(search, 0, 1).getResults().next();
  // }
  //
  // @Override
  // public Flux<BaleenMention> getAll(final int offset, final int limit) {
  // return search(MentionSearch.builder().build(), offset, limit).getResults();
  // }
  //
  //
  // @Override
  // public Mono<Long> count() {
  // return aggregateOverMentions(CountOutcome.class, Optional.empty(),
  // Aggregation.count().as("total"))
  // .next()
  // .map(CountOutcome::getTotal);
  // }
  //
  // @Override
  // public Flux<TermBin> countByField(final Optional<MentionFilter> filter, final List<String> path,
  // final int limit) {
  //
  // // There's no nesting mention properties (sadly!)... so the field is just the last path segment
  // final String field = path.get(path.size() - 1);
  //
  // return aggregateOverMentions(TermBin.class,
  // filter,
  // group(field).count().as("count"),
  // Aggregation.project("count").and("_id").as("term"));
  // }
  //
  // @Override
  // public MentionSearchResult search(final MentionSearch search, final int offset, final int limit)
  // {
  //
  // final Flux<BaleenMention> results = aggregateOverMentions(Document.class,
  // Optional.ofNullable(search.getMentionFilter()))
  // .skip(offset)
  // .take(limit)
  // .map(MongoMention::new)
  // .map(MongoMention::toMention);
  //
  // return new MentionSearchResult(results, Mono.empty());
  // }

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
  public Flux<TermBin> countByField(final Optional<MentionFilter> filter, final List<String> path,
      final int size) {
    return termAggregation(MentionFilters.createFilter(filter, "", false), path, size);
  }

  @Override
  public MentionSearchResult search(final MentionSearch mentionSearch, final int offset, final int size) {

    final Optional<Bson> filter = MentionFilters.createFilter(mentionSearch);

    final Mono<Long> total;
    final Flux<BaleenMention> flux;
    if (filter.isPresent()) {
      total = toMono(getCollection().count(filter.get()));
      flux = toFlux(getCollection().find(filter.get()))
          .skip(offset)
          .take(size)
          .map(Converters::toBaleenMention);
    } else {
      total = count();
      flux = getAll(offset, size);
    }

    return new MentionSearchResult(flux, total);
  }
}
