package io.committed.ketos.data.elasticsearch.providers;

import java.util.Optional;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.ketos.common.baleenconsumer.OutputDocument;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchMetadataProvider
    extends AbstractElasticsearchServiceDataProvider<OutputDocument, EsDocumentService>
    implements MetadataProvider {


  public ElasticsearchMetadataProvider(final String dataset, final String datasource,
      final EsDocumentService documentService) {
    super(dataset, datasource, documentService);
  }

  @Override
  public Flux<TermBin> countByKey(final Optional<String> key, final int size) {
    if (key.isPresent()) {
      // If the key is present then we are just counting the number of documents with that key
      final Mono<Long> count =
          getService()
              .count(queryByKey(key.get()));

      return count.flux().map(c -> new TermBin(key.get(), c));
    } else {
      // if the key is not present we are calculating doc counts for all keys
      return aggregateByMetadata(Optional.empty(), BaleenProperties.METADATA_KEY);
    }
  }

  @Override
  public Flux<TermBin> countByValue(final Optional<String> key, final int size) {
    return aggregateByMetadata(key.map(this::queryByKey), BaleenProperties.METADATA_VALUE);
  }

  private Flux<TermBin> aggregateByMetadata(final Optional<QueryBuilder> query, final String field) {

    final String fieldkeyword = BaleenProperties.METADATA + "." + field;

    final NativeSearchQueryBuilder searchQueryBuilder = getService().queryBuilder()
        .addAggregation(AggregationBuilders.nested("agg", BaleenProperties.METADATA)
            .subAggregation(AggregationBuilders.filter("filtered", query.orElse(QueryBuilders.matchAllQuery()))
                .subAggregation(AggregationBuilders.terms("count").field(fieldkeyword))));


    return getService().query(searchQueryBuilder, response -> {
      final Nested nested = response.getAggregations().get("agg");
      final Filter filtered = nested.getAggregations().get("filtered");
      final Terms terms = filtered.getAggregations().get("count");
      return Flux.fromIterable(terms.getBuckets())
          .map(b -> new TermBin(b.getKeyAsString(), b.getDocCount()));

    });
  }

  private NestedQueryBuilder queryByKey(final String key) {
    return QueryBuilders.nestedQuery(BaleenProperties.METADATA,
        QueryBuilders.matchQuery(BaleenProperties.METADATA + "." + BaleenProperties.METADATA_KEY, key), ScoreMode.None);
  }

}
