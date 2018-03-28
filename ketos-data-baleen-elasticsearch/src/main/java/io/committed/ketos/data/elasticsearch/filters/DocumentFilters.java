package io.committed.ketos.data.elasticsearch.filters;

import java.util.Optional;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;

import io.committed.invest.core.utils.FieldUtils;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentFilter.DocumentInfoFilter;
import io.committed.ketos.common.graphql.output.DocumentSearch;
import io.committed.ketos.common.utils.ValueConversion;

/** Convert Ketos Document queries to ES queries. */
public final class DocumentFilters {

  private DocumentFilters() {
    // Singleton
  }

  public static Optional<QueryBuilder> toQuery(final Optional<DocumentFilter> filter) {
    return filter.flatMap(DocumentFilters::toQuery);
  }

  public static Optional<QueryBuilder> toQuery(final DocumentFilter filter) {

    final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

    ValueConversion.stringValue(filter.getContent())
        .map(s -> QueryBuilders.queryStringQuery(s).defaultField(BaleenProperties.CONTENT))
        .ifPresent(queryBuilder::must);

    ValueConversion.stringValue(filter.getId())
        .map(s -> QueryBuilders.termQuery(BaleenProperties.EXTERNAL_ID, s))
        .ifPresent(queryBuilder::must);

    if (filter.getInfo() != null) {
      final DocumentInfoFilter info = filter.getInfo();

      ValueConversion.stringValue(info.getCaveats())
          .map(
              s ->
                  QueryBuilders.matchQuery(
                      FieldUtils.joinField(BaleenProperties.PROPERTIES, BaleenProperties.CAVEATS),
                      s))
          .ifPresent(queryBuilder::must);

      ValueConversion.stringValue(info.getClassification())
          .map(
              s ->
                  QueryBuilders.matchQuery(
                      FieldUtils.joinField(
                          BaleenProperties.PROPERTIES, BaleenProperties.CLASSIFICATION),
                      s))
          .ifPresent(queryBuilder::must);

      ValueConversion.stringValue(info.getLanguage())
          .map(
              s ->
                  QueryBuilders.matchQuery(
                      FieldUtils.joinField(BaleenProperties.PROPERTIES, BaleenProperties.LANGUAGE),
                      s))
          .ifPresent(queryBuilder::must);

      ValueConversion.stringValue(info.getReleasability())
          .map(
              s ->
                  QueryBuilders.matchQuery(
                      FieldUtils.joinField(
                          BaleenProperties.PROPERTIES, BaleenProperties.RELEASABILITY),
                      s))
          .ifPresent(queryBuilder::must);

      ValueConversion.stringValue(info.getSource())
          .map(
              s ->
                  QueryBuilders.matchQuery(
                      FieldUtils.joinField(BaleenProperties.PROPERTIES, BaleenProperties.SOURCE),
                      s))
          .ifPresent(queryBuilder::must);

      if (info.getStartTimestamp() != null) {
        queryBuilder.must(
            QueryBuilders.rangeQuery(
                    FieldUtils.joinField(BaleenProperties.PROPERTIES, BaleenProperties.TIMESTAMP))
                .gte(info.getStartTimestamp().getTime()));
      }

      if (info.getEndTimestamp() != null) {
        queryBuilder.must(
            QueryBuilders.rangeQuery(
                    FieldUtils.joinField(BaleenProperties.PROPERTIES, BaleenProperties.TIMESTAMP))
                .lte(info.getEndTimestamp().getTime()));
      }

      ValueConversion.stringValue(info.getType())
          .map(
              s ->
                  QueryBuilders.matchQuery(
                      FieldUtils.joinField(
                          BaleenProperties.PROPERTIES, BaleenProperties.DOCUMENT_TYPE),
                      s))
          .ifPresent(queryBuilder::must);

      ValueConversion.stringValue(info.getPublishedId())
          .map(
              s ->
                  QueryBuilders.matchQuery(
                      FieldUtils.joinField(
                          BaleenProperties.PROPERTIES, BaleenProperties.PUBLISHED_IDS + ".id"),
                      s))
          .ifPresent(queryBuilder::must);
    }

    if (filter.getMetadata() != null) {
      filter
          .getMetadata()
          .stream()
          .filter(p -> ValueConversion.isValueOrOther(p.getValue()))
          .map(
              e ->
                  QueryBuilders.nestedQuery(
                      BaleenProperties.METADATA,
                      QueryBuilders.boolQuery()
                          .must(
                              QueryBuilders.matchQuery(
                                  BaleenProperties.METADATA + "." + BaleenProperties.METADATA_KEY,
                                  e.getKey()))
                          .must(
                              QueryBuilders.matchQuery(
                                  BaleenProperties.METADATA + "." + BaleenProperties.METADATA_VALUE,
                                  ValueConversion.valueOrNull(e.getValue()))),
                      ScoreMode.Max))
          .forEach(queryBuilder::must);
    }

    if (filter.getProperties() != null) {
      filter
          .getProperties()
          .stream()
          .filter(p -> ValueConversion.isValueOrOther(p.getValue()))
          .map(
              e ->
                  QueryBuilders.matchQuery(
                      BaleenProperties.PROPERTIES + "." + e.getKey(),
                      ValueConversion.valueOrNull(e.getValue())))
          .forEach(queryBuilder::must);
    }

    return Optional.of(queryBuilder);
  }

  public static Optional<QueryBuilder> toQuery(
      final DocumentSearch documentSearch,
      final String mentionType,
      final String entityType,
      final String relationType) {
    final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

    if (documentSearch.getDocumentFilter() != null) {
      DocumentFilters.toQuery(documentSearch.getDocumentFilter()).ifPresent(queryBuilder::must);
    }

    if (documentSearch.getMentionFilters() != null) {
      documentSearch
          .getMentionFilters()
          .stream()
          .map(f -> MentionFilters.toQuery(Optional.ofNullable(f), ""))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .forEach(
              q ->
                  queryBuilder.must(
                      JoinQueryBuilders.hasChildQuery(mentionType, q, ScoreMode.Max)));
    }

    if (documentSearch.getEntityFilters() != null) {
      documentSearch
          .getEntityFilters()
          .stream()
          .map(f -> EntityFilters.toQuery(Optional.ofNullable(f), ""))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .forEach(
              q ->
                  queryBuilder.must(JoinQueryBuilders.hasChildQuery(entityType, q, ScoreMode.Max)));
    }

    if (documentSearch.getRelationFilters() != null) {
      documentSearch
          .getRelationFilters()
          .stream()
          .map(f -> RelationFilters.toQuery(Optional.ofNullable(f), ""))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .forEach(
              q ->
                  queryBuilder.must(
                      JoinQueryBuilders.hasChildQuery(relationType, q, ScoreMode.Max)));
    }

    return Optional.of(queryBuilder);
  }
}
