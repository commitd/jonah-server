package io.committed.ketos.plugins.data.mongo.filters;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentFilter.DocumentInfoFilter;

public final class DocumentFilters {

  private DocumentFilters() {
    // Singleton
  }

  public static Optional<Bson> createFilter(final Optional<DocumentFilter> documentFilter) {
    return documentFilter.flatMap(DocumentFilters::createFilter);
  }

  public static Optional<Bson> createFilter(final DocumentFilter documentFilter) {

    if (documentFilter == null) {
      return Optional.empty();
    }

    final List<Bson> filters = new LinkedList<>();

    // Text search must be first (in aggregations)
    if (documentFilter.getContent() != null) {
      filters.add(Filters.text(documentFilter.getContent()));
    }


    if (documentFilter.getInfo() != null) {
      final DocumentInfoFilter info = documentFilter.getInfo();
      if (info.getCaveats() != null) {
        filters.add(Filters.in("properties.caveats", info.getCaveats()));
      }

      if (info.getReleasability() != null) {
        filters.add(Filters.in("properties.releasability", info.getCaveats()));
      }

      if (info.getEndTimestamp() != null) {
        filters.add(Filters.lte("properties.timestamp", info.getEndTimestamp()));
      }

      if (info.getStartTimestamp() != null) {
        filters.add(Filters.gte("properties.timestamp", info.getStartTimestamp()));
      }

      if (info.getLanguage() != null) {
        filters.add(Filters.eq("properties.language", info.getLanguage()));
      }

      if (info.getSource() != null) {
        filters.add(Filters.eq("properties.source", info.getSource()));
      }

      if (info.getType() != null) {
        filters.add(Filters.eq("properties.type", info.getType()));
      }

      if (info.getPublishedId() != null) {
        filters.add(Filters.in("properties.publishedIds.id", info.getPublishedId()));
      }

    }

    if (documentFilter.getMetadata() != null) {
      for (final Map.Entry<String, Object> e : documentFilter.getMetadata().entrySet()) {
        filters.add(Filters.elemMatch("metadata", Filters.and(
            Filters.eq("metadata.key", e.getKey()),
            Filters.eq("metadata.value", e.getValue()))));

      }
    }

    if (documentFilter.getProperties() != null) {
      for (final Map.Entry<String, Object> e : documentFilter.getProperties().entrySet()) {
        filters.add(Filters.eq("propertiers." + e.getKey(), e.getValue()));
      }
    }


    if (filters.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(Filters.and(filters));
  }

}
