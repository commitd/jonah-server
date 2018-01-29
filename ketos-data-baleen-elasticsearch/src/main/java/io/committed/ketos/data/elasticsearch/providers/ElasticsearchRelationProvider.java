package io.committed.ketos.data.elasticsearch.providers;

import java.util.List;
import java.util.Optional;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.metrics.sum.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.graphql.input.RelationFilter;
import io.committed.ketos.common.graphql.intermediate.RelationSearchResult;
import io.committed.ketos.common.graphql.output.RelationSearch;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import io.committed.ketos.data.elasticsearch.dao.EsRelation;
import io.committed.ketos.data.elasticsearch.filters.RelationFilters;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchRelationProvider
    extends AbstractElasticsearchServiceDataProvider<EsDocument, EsDocumentService>
    implements RelationProvider {

  // WE have to give some form of limit in ES...
  private static final int MAX_RELATIONS = 1000;

  public ElasticsearchRelationProvider(final String dataset, final String datasource,
      final EsDocumentService relationService) {
    super(dataset, datasource, relationService);
  }


  @Override
  public Flux<BaleenRelation> getRelations(final BaleenDocument document) {
    return getService()
        .getById(document.getId())
        .flatMapMany(d -> Flux.fromIterable(d.getRelations()))
        .map(EsRelation::toBaleenRelation);
  }


  @Override
  public Flux<BaleenRelation> getAll(final int offset, final int limit) {
    return findRelations(Optional.empty(), offset, limit);
  }


  @Override
  public Flux<BaleenRelation> getSourceRelations(final BaleenMention mention) {
    return getService().search(QueryBuilders.termQuery("relations.source", mention.getId()), 0, MAX_RELATIONS)
        .flatMap(d -> Flux.fromIterable(d.getRelations()))
        .map(EsRelation::toBaleenRelation);
  }

  @Override
  public Flux<BaleenRelation> getTargetRelations(final BaleenMention mention) {
    return getService().search(QueryBuilders.termQuery("relations.target", mention.getId()), 0, MAX_RELATIONS)
        .flatMap(d -> Flux.fromIterable(d.getRelations()))
        .map(EsRelation::toBaleenRelation);
  }

  @Override
  public Mono<BaleenRelation> getById(final String id) {
    return getService().search(QueryBuilders.termQuery("relations.externalId", id), 0, 1)
        .flatMap(d -> Flux.fromIterable(d.getRelations()))
        // We have a document which contains the right value ... but it has lots of relations
        .filter(r -> r.getId() != null && r.getId().equals("id"))
        .next()
        .map(EsRelation::toBaleenRelation);
  }

  @Override
  public Mono<Long> count() {
    // We need a script here in order to calculate the answer...
    // TODO: ideally we'd change the consumer to produce the number as part of the output!

    final NativeSearchQueryBuilder qb = getService().queryBuilder()
        .addAggregation(
            new SumAggregationBuilder("agg").script(new Script("doc." + EsDocument.RELATIONS_PREFIX + ".length")));

    return getService().query(qb, response -> {
      final ParsedSum sum = response.getAggregations().get("agg");
      return Mono.just((long) sum.getValue());
    });
  }



  @Override
  public Flux<TermBin> countByField(final Optional<RelationFilter> filter, final List<String> path, final int limit) {
    final Optional<QueryBuilder> query = RelationFilters.toQuery(filter, EsDocument.RELATIONS_PREFIX);

    final String field = path.get(path.size() - 1);

    // TODO: I think this is the wrong count. It's the number of documents which have that
    // rather than the number of relations? Might need a nested aggrgation?

    return getService().termAggregation(query, field, limit);
  }

  @Override
  public RelationSearchResult search(final RelationSearch search, final int offset, final int limit) {
    final Optional<QueryBuilder> query =
        RelationFilters.toQuery(search.getRelationFilter(), EsDocument.RELATIONS_PREFIX);
    return new RelationSearchResult(findRelations(query, offset, limit), Mono.empty());
  }


  private Flux<BaleenRelation> findRelations(final Optional<QueryBuilder> query, final int offset, final int limit) {
    // We get all the documents which have a relation from 0 to offset + limit
    // that way we know we have at least limit number of relations.


    final QueryBuilder hasRelations = QueryBuilders.existsQuery(EsDocument.RELATIONS_PREFIX);
    final QueryBuilder qb;

    if (query.isPresent()) {
      qb = QueryBuilders.boolQuery().must(hasRelations).must(query.get());
    } else {
      qb = hasRelations;
    }

    return getService().search(qb, 0, offset + limit)
        .flatMap(d -> Flux.fromIterable(d.getRelations()))
        .skip(offset)
        .take(limit)
        .map(EsRelation::toBaleenRelation);
  }


}
