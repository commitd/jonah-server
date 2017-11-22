package io.committed.ketos.graphql.baleen.calculated;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.List;
import java.util.Map;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentInfo;
import io.committed.ketos.graphql.baleen.utils.BaleenUtils;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;

@VesselGraphQlService
public class DocumentTitleField {

  @GraphQLQuery(name = "title",
      description = "Document title derived from metadata, published ids, document path, etc")
  public String title(@GraphQLContext final BaleenDocument document) {

    final Map<String, Object> metadata = document.getMetadata();

    if (metadata != null) {
      final String title = lookInMetadata(metadata);
      if (!isEmpty(title)) {
        return title;
      }
    }

    // Published Id?
    if (document.getPublishedIds() != null) {
      final String title = lookInPublishedIds(document.getPublishedIds());
      if (!isEmpty(title)) {
        return title;
      }
    }

    // Fall back to last bit of path
    if (document.getInfo() != null) {
      final String title = lookInInfo(document.getInfo());
      if (!isEmpty(title)) {
        return title;
      }
    }

    return document.getId() != null ? document.getId() : "Untitled";

  }

  private String lookInInfo(final BaleenDocumentInfo info) {
    final String source = info.getSource();
    if (source != null && !source.isEmpty()) {
      // Is this a path? (best guess)?
      if (source.contains("/")) {
        final String title = extractFromLastPathSegment(source, '/');
        if (!isEmpty(title)) {
          return title;
        }
      }

      if (source.contains("\\")) {
        final String title = extractFromLastPathSegment(source, '\\');
        if (!isEmpty(title)) {
          return title;
        }
      }
    }
    return null;
  }

  private String extractFromLastPathSegment(final String source, final char seperator) {
    final int index = source.lastIndexOf(seperator, source.length() - 1);
    if (index < source.length() - 1) {
      final String filename = source.substring(index + 1);

      final int extensionIndex = filename.indexOf('.');
      // doesn't start with it and exists
      if (extensionIndex > 0) {
        return filename.substring(0, extensionIndex);
      } else {
        return filename;
      }
    }
    return null;
  }

  private String lookInPublishedIds(final List<String> publishedIds) {
    for (final String s : publishedIds) {
      if (!isEmpty(s)) {
        return s;
      }
    }
    return null;
  }

  private String lookInMetadata(final Map<String, Object> metadata) {
    String title = null;

    title = BaleenUtils.getAsMetadataKey(metadata, "title");
    if (!isEmpty(title)) {
      return title;
    }

    title = BaleenUtils.getAsMetadataKey(metadata, "DC.Title");
    if (!isEmpty(title)) {
      return title;
    }

    return null;
  }

}
