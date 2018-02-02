package io.committed.ketos.data.elasticsearch.providers;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.ketos.common.providers.baleen.MetadataProvider;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchMetadataProvider
    extends AbstractElasticsearchServiceDataProvider<EsDocument, EsDocumentService>
    implements MetadataProvider {


  public ElasticsearchMetadataProvider(final String dataset, final String datasource,
      final EsDocumentService documentService) {
    super(dataset, datasource, documentService);
  }

  @Override
  public Flux<TermBin> countByKey(final Optional<String> key, final int size) {


    if (key.isPresent()) {
      // If the key is present then we are just counting the number of documents with that key
      return countByMetadataKey(Collections.singleton(key.get()));

    } else {
      // if the key is not present we are calculating doc counts for all keys
      final Set<String> keys = getKeys();

      return countByMetadataKey(keys)
          .sort((a, b) -> Long.compare(a.getCount(), b.getCount()))
          .take(size);
    }
  }


  @Override
  public Flux<TermBin> countByValue(final Optional<String> key, final int size) {
    // TODO: Change this API, you count by value for a single key, anything else is silly
    if (!key.isPresent()) {
      return Flux.empty();
    }

    return aggregateByMetadata(key.get());
  }

  private Flux<TermBin> countByMetadataKey(final Collection<String> keys) {
    return Flux.fromIterable(keys)
        .flatMap(key -> {
          final Mono<Long> count = getService().count(
              QueryBuilders.nestedQuery(EsDocument.METADATA,
                  QueryBuilders.existsQuery(EsDocument.METADATA_PREFIX + key), ScoreMode.None));
          return count.map(c -> new TermBin(key, c));
        });
  }

  private Flux<TermBin> aggregateByMetadata(final String key) {

    final String fieldkeyword = "metadata." + key + ".keyword";

    final NativeSearchQueryBuilder searchQueryBuilder = getService().queryBuilder()
        .addAggregation(AggregationBuilders.nested("agg", EsDocument.METADATA)
            .subAggregation(AggregationBuilders.terms("count").field(fieldkeyword)));


    return getService().query(searchQueryBuilder, response -> {
      final Nested nested = response.getAggregations().get("agg");

      final Terms terms = nested.getAggregations().get("count");
      return Flux.fromIterable(terms.getBuckets())
          .map(b -> new TermBin(b.getKeyAsString(), b.getDocCount()));

    });
  }

  private Set<String> getKeys() {
    final Map<String, Object> mapping = getService().getMapping();
    if (mapping == null) {
      return Collections.emptySet();
    }
    final Map<String, Object> rootProperties = (Map<String, Object>) mapping.get("properties");
    if (rootProperties == null) {
      return Collections.emptySet();
    }

    final Map<String, Object> metadataMapping = (Map<String, Object>) rootProperties.get("metadata");
    if (metadataMapping == null) {
      return Collections.emptySet();
    }
    final Map<String, Object> metadataProperties = (Map<String, Object>) metadataMapping.get("properties");
    if (metadataProperties == null) {
      return Collections.emptySet();
    }
    return metadataProperties.keySet();
  }

  // @formatter:off


/* This code is right if you have metadata: [{key, value}] is nested
 * I leave it here as we might change back to it!
 *
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

      return aggregateByMetadata(Optional.empty(), "key");
    }
  }

  @Override
  public Flux<TermBin> countByValue(final Optional<String> key, final int size) {
    return aggregateByMetadata(key.map(this::queryByKey), "value");
  }

  private Flux<TermBin> aggregateByMetadata(final Optional<QueryBuilder> query, final String field) {

    final String fieldkeyword = "metadata." + field + ".keyword";

    NativeSearchQueryBuilder searchQueryBuilder = getService().queryBuilder()
        .addAggregation(AggregationBuilders.nested("agg", "metadata")
            .subAggregation(AggregationBuilders.terms("count").field(fieldkeyword)));

    if (query.isPresent()) {
      searchQueryBuilder = searchQueryBuilder.withQuery(query.get());
    }

    return getService().query(searchQueryBuilder, response -> {
      final Nested nested = response.getAggregations().get("agg");

      final Terms terms = nested.getAggregations().get("count");
      return Flux.fromIterable(terms.getBuckets())
          .map(b -> new TermBin(b.getKeyAsString(), b.getDocCount()));

    });
  }

  private NestedQueryBuilder queryByKey(final String key) {
    return QueryBuilders.nestedQuery("metadata", QueryBuilders.termQuery("metadata.key", key), ScoreMode.None);
  }
*/
//@formatter:on

}
