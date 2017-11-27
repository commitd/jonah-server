package io.committed.ketos.data.elasticsearch.repository;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.metrics.sum.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.committed.ketos.data.elasticsearch.dao.EsRelation;
import io.committed.vessel.utils.OffsetLimitPagable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EsRelationService extends AbstractEsService {

  public EsRelationService(final ObjectMapper mapper, final ElasticsearchTemplate elastic) {
    super(mapper, elastic);
  }

  public Flux<EsRelation> findAll(final int offset, final int limit) {
    // The offset and limit calculations here are very rough...
    // we are search against documents which have at least 1 relation
    // and so we just need to do a worst case coarse pagable here
    // and then skip/take when we've mapped into relations
    // TODO: It might be possible to do something with nested objects, but the whole ES
    // consumer needs to be redone.

    return getElastic()
        .query(
            queryBuilder()
                .withQuery(QueryBuilders.existsQuery("relations"))
                .withPageable(new OffsetLimitPagable(0, offset + limit))
                .build(),
            resultsToDocumentExtractor())
        .flatMap(d -> Flux.fromIterable(d.getRelations()))
        .skip(offset)
        .take(limit);
  }

  public Flux<EsRelation> findByDocument(final String id) {
    return getDocumentById(id)
        .flatMapMany(d -> Flux.fromIterable(d.getRelations()));
  }

  public Flux<EsRelation> findBySource(final String entityId) {
    return getElastic()
        .query(
            queryBuilder().withQuery(QueryBuilders.termQuery("relations.target", entityId))
                .build(),
            resultsToDocumentExtractor())
        .flatMap(f -> Flux.fromIterable(f.getRelations()))
        .filter(e -> entityId.equals(e.getSource()));
  }

  public Flux<EsRelation> findByTarget(final String entityId) {
    return getElastic()
        .query(
            queryBuilder().withQuery(QueryBuilders.termQuery("relations.source", entityId))
                .build(),
            resultsToDocumentExtractor())
        .flatMap(f -> Flux.fromIterable(f.getRelations()))
        .filter(e -> entityId.equals(e.getSource()));
  }

  public Mono<EsRelation> getById(final String id) {
    return getElastic()
        .query(
            queryBuilder().withQuery(QueryBuilders.termQuery("relations.externalId", id)).build(),
            resultsToDocumentExtractor())
        .flatMap(f -> Flux.fromIterable(f.getRelations()))
        .filter(e -> id.equals(e.getExternalId()))
        .next();
  }

  public Mono<Long> count() {
    // TODO: We need a script here in order to calculate the answer...
    // ideally we'd change the consumer to produce a different number.

    final NativeSearchQuery query = queryBuilder()
        .addAggregation(new SumAggregationBuilder("agg").script(new Script("doc.relations.length")))
        .build();

    return getElastic().query(query, response -> {
      final ParsedSum sum = (ParsedSum) response.getAggregations().get("agg");
      return Mono.just((long) sum.getValue());
    });
  }


}
