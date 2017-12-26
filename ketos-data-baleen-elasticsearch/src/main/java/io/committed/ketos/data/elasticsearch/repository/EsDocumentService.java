package io.committed.ketos.data.elasticsearch.repository;

import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EsDocumentService extends AbstractEsService {

  public EsDocumentService(final ObjectMapper mapper, final ElasticsearchTemplate elastic) {
    super(mapper, elastic);
  }

  public Mono<EsDocument> findById(final String id) {
    return getDocumentById(id);
  }

  public Flux<EsDocument> search(final String search, final int offset, final int limit) {
    return searchForDocuments(search, offset, limit);
  }


  public Mono<Long> countSearchMatches(final String query) {
    return Mono.just(getElastic().count(queryBuilder()
        .withQuery(QueryBuilders.queryStringQuery(query))
        .build()));
  }

  public Mono<Long> count() {
    return Mono.just(getElastic().count(queryBuilder()
        .withQuery(QueryBuilders.matchAllQuery())
        .build()));
  }

  public Flux<TermBin> countByType() {
    return termAggregation("docType");
  }

  public Flux<TermBin> countByClassification() {
    return termAggregation("classification");

  }

  public Flux<TermBin> countByLanguage() {
    return termAggregation("language");

  }

  public Flux<TimeBin> countByDate() {
    return timelineAggregation("dateAccessed");
  }

}
