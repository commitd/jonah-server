package io.committed.ketos.data.elasticsearch.repository;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.metrics.sum.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.elasticsearch.AbstractEsService;
import io.committed.ketos.data.elasticsearch.dao.BaleenElasticsearchConstants;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import io.committed.ketos.data.elasticsearch.dao.EsEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EsEntityService extends AbstractEsService<EsDocument> {

  public EsEntityService(final ObjectMapper mapper, final ElasticsearchTemplate elastic) {
    super(mapper, elastic, BaleenElasticsearchConstants.DOCUMENT_INDEX,
        BaleenElasticsearchConstants.DOCUMENT_TYPE, EsDocument.class);
  }

  public Mono<EsEntity> getById(final String id) {
    return getElastic()
        .query(queryBuilder().withQuery(QueryBuilders.termQuery("entities.externalId", id)).build(),
            resultsToDocumentExtractor())
        .flatMap(f -> Flux.fromIterable(f.getEntities()))
        .filter(e -> id.equals(e.getExternalId()))
        .next();
  }

  public Flux<EsEntity> findByDocumentId(final String id) {
    return getDocumentById(id)
        .flatMapMany(f -> Flux.fromIterable(f.getEntities()));
  }

  public Mono<Long> count() {
    // TODO: We need a script here in order to calculate the answer...
    // ideally we'd change the consumer to produce a different number.

    final NativeSearchQuery query = queryBuilder()
        .addAggregation(new SumAggregationBuilder("agg").script(new Script("doc.entities.length")))
        .build();

    return getElastic().query(query, response -> {
      final ParsedSum sum = (ParsedSum) response.getAggregations().get("agg");
      return Mono.just((long) sum.getValue());
    });
  }

  public Flux<TermBin> countByType() {
    return termAggregation("entities.type");
  }

}
