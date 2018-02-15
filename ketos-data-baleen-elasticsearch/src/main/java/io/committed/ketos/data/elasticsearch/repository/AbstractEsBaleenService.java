package io.committed.ketos.data.elasticsearch.repository;

import java.util.List;
import java.util.Optional;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.elasticsearch.ElasticsearchSupportService;
import io.committed.invest.support.data.utils.FieldUtils;
import io.committed.ketos.common.constants.BaleenProperties;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractEsBaleenService<T> extends ElasticsearchSupportService<T> {

  // We have to provide an upper value...
  private static final int ALL_RESULTS = 10000;

  public AbstractEsBaleenService(final ObjectMapper mapper, final ElasticsearchTemplate elastic, final String indexName,
      final String typeName, final Class<T> clazz) {
    super(mapper, elastic, indexName, typeName, clazz);
  }

  public Mono<T> getByExternalId(final String id) {
    return search(QueryBuilders.matchQuery(BaleenProperties.EXTERNAL_ID, id), 0, 1).next();
  }

  public Flux<T> getByDocumentId(final String id) {
    return search(QueryBuilders.matchQuery(BaleenProperties.DOC_ID, id), 0, ALL_RESULTS);
  }


  public Flux<TermBin> termAggregation(final Optional<QueryBuilder> query, final List<String> path,
      final int size) {
    final String field = FieldUtils.joinField(path);
    return termAggregation(query, field, size);
  }


}
