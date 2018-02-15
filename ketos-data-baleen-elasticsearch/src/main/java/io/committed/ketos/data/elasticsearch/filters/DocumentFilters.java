package io.committed.ketos.data.elasticsearch.filters;

import java.util.Optional;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;
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
      queryBuilder.must(QueryBuilders.queryStringQuery(filter.getContent()).defaultField("content"));
    }

    if (filter.getId() != null) {
      queryBuilder.must(QueryBuilders.termQuery("externalId", filter.getId()));
    }


    if (filter.getInfo() != null) {
      final DocumentInfoFilter info = filter.getInfo();

      if (info.getCaveats() != null) {
        queryBuilder.must(QueryBuilders.matchQuery("caveats", info.getCaveats()));
      }

      if (info.getClassification() != null) {
        queryBuilder.must(QueryBuilders.matchQuery("classification", info.getClassification()));
      }

      if (info.getLanguage() != null) {
        queryBuilder.must(QueryBuilders.matchQuery("language", info.getLanguage()));
      }

      if (info.getReleasability() != null) {
        queryBuilder.must(QueryBuilders.matchQuery("releasability", info.getReleasability()));
      }

      if (info.getSource() != null) {
        queryBuilder.must(QueryBuilders.termQuery("sourceUri", info.getSource()));
      }

      if (info.getStartTimestamp() != null) {
        queryBuilder.must(QueryBuilders.rangeQuery("dateAccessed").gte(info.getStartTimestamp()));
      }

      if (info.getStartTimestamp() != null) {
        queryBuilder.must(QueryBuilders.rangeQuery("dateAccessed").lte(info.getEndTimestamp()));
      }

      if (info.getType() != null) {
        queryBuilder.must(QueryBuilders.matchQuery("docType", info.getSource()));
      }

      if (info.getPublishedId() != null) {
        queryBuilder.must(QueryBuilders.matchQuery("publishedIds", info.getPublishedId()));
      }
    }

    if (filter.getMetadata() != null) {
      filter.getMetadata().entrySet().forEach(e -> {
        queryBuilder.must(QueryBuilders.nestedQuery("metadata",
            QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("metadata.key", e.getKey()))
                .must(QueryBuilders.matchQuery("metadata.value", e.getValue())),
            ScoreMode.None));
      });
    }

    if (filter.getProperties() != null) {
      filter.getProperties().entrySet().forEach(e -> {
        queryBuilder.must(QueryBuilders.matchQuery("properties." + e.getKey(), e.getValue()));
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
          .map(f -> MentionFilters.toMentionsQuery(f, ""))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .forEach(q -> queryBuilder.must(JoinQueryBuilders.hasChildQuery(
              mentionType,
              q,
              ScoreMode.None)));
    }

    if (documentSearch.getRelationFilters() != null) {
      documentSearch.getRelationFilters().stream()
          .map(f -> RelationFilters.toQuery(f, ""))
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
