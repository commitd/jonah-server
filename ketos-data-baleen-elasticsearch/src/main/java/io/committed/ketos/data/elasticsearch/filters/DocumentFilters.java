package io.committed.ketos.data.elasticsearch.filters;

import java.util.Optional;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentFilter.DocumentInfoFilter;
import io.committed.ketos.common.graphql.output.DocumentSearch;

public final class DocumentFilters {

  private DocumentFilters() {
    // Singleton
  }

  public static Optional<QueryBuilder> toQuery(final Optional<DocumentFilter> filter) {
    return filter.flatMap(DocumentFilters::toQuery);
  }

  public static Optional<QueryBuilder> toQuery(final DocumentFilter filter) {

    final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

    if (filter.getContent() != null) {
      queryBuilder.must(QueryBuilders.queryStringQuery(filter.getContent()).defaultField(BaleenProperties.CONTENT));
    }

    if (filter.getId() != null) {
      queryBuilder.must(QueryBuilders.termQuery(BaleenProperties.EXTERNAL_ID, filter.getId()));
    }


    if (filter.getInfo() != null) {
      final DocumentInfoFilter info = filter.getInfo();

      if (info.getCaveats() != null) {
        queryBuilder.must(QueryBuilders.matchQuery(BaleenProperties.CAVEATS, info.getCaveats()));
      }

      if (info.getClassification() != null) {
        queryBuilder.must(QueryBuilders.matchQuery(BaleenProperties.CLASSIFICATION, info.getClassification()));
      }

      if (info.getLanguage() != null) {
        queryBuilder.must(QueryBuilders.matchQuery(BaleenProperties.LANGUAGE, info.getLanguage()));
      }

      if (info.getReleasability() != null) {
        queryBuilder.must(QueryBuilders.matchQuery(BaleenProperties.RELEASABILITY, info.getReleasability()));
      }

      if (info.getSource() != null) {
        queryBuilder.must(QueryBuilders.termQuery(BaleenProperties.SOURCE, info.getSource()));
      }

      if (info.getStartTimestamp() != null) {
        queryBuilder.must(QueryBuilders.rangeQuery(BaleenProperties.TIMESTAMP).gte(info.getStartTimestamp()));
      }

      if (info.getStartTimestamp() != null) {
        queryBuilder.must(QueryBuilders.rangeQuery(BaleenProperties.TIMESTAMP).lte(info.getEndTimestamp()));
      }

      if (info.getType() != null) {
        queryBuilder.must(QueryBuilders.matchQuery(BaleenProperties.DOCUMENT_TYPE, info.getSource()));
      }

      if (info.getPublishedId() != null) {
        queryBuilder.must(QueryBuilders.matchQuery(BaleenProperties.PUBLISHED_IDS + ".id", info.getPublishedId()));
      }
    }

    if (filter.getMetadata() != null) {
      filter.getMetadata().stream().forEach(e -> {
        queryBuilder.must(QueryBuilders.nestedQuery(BaleenProperties.METADATA,
            QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(BaleenProperties.METADATA + "." + BaleenProperties.METADATA_KEY,
                    e.getKey()))
                .must(QueryBuilders.matchQuery(BaleenProperties.METADATA + "." + BaleenProperties.METADATA_VALUE,
                    e.getValue())),
            ScoreMode.None));
      });
    }

    if (filter.getProperties() != null) {
      filter.getProperties().stream().forEach(e -> {
        queryBuilder.must(QueryBuilders.matchQuery(BaleenProperties.PROPERTIES + "." + e.getKey(), e.getValue()));
      });
    }

    return Optional.of(queryBuilder);

  }

  public static Optional<QueryBuilder> toQuery(final DocumentSearch documentSearch, final String mentionType,
      final String entityType,
      final String relationType) {
    final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

    if (documentSearch.getDocumentFilter() != null) {
      DocumentFilters.toQuery(documentSearch.getDocumentFilter())
          .ifPresent(queryBuilder::must);
    }

    if (documentSearch.getMentionFilters() != null) {
      documentSearch.getMentionFilters().stream()
          .map(f -> MentionFilters.toQuery(Optional.ofNullable(f), ""))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .forEach(q -> queryBuilder.must(JoinQueryBuilders.hasChildQuery(
              mentionType,
              q,
              ScoreMode.None)));
    }

    if (documentSearch.getEntityFilters() != null) {
      documentSearch.getEntityFilters().stream()
          .map(f -> EntityFilters.toQuery(Optional.ofNullable(f), ""))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .forEach(q -> queryBuilder.must(JoinQueryBuilders.hasChildQuery(
              entityType,
              q,
              ScoreMode.None)));
    }

    if (documentSearch.getRelationFilters() != null) {
      documentSearch.getRelationFilters().stream()
          .map(f -> RelationFilters.toQuery(Optional.ofNullable(f), ""))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .forEach(q -> queryBuilder.must(JoinQueryBuilders.hasChildQuery(
              relationType,
              q,
              ScoreMode.None)));
    }

    return Optional.of(queryBuilder);
  }
}
