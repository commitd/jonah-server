package io.committed.ketos.graphql.baleen.calculated;

import static org.springframework.util.StringUtils.isEmpty;
import java.util.Map;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.graphql.baleen.utils.BaleenUtils;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;

@GraphQLService
public class DocumentSummaryField {

  private static final String ELLIPSES = "...";

  @GraphQLQuery(name = "summary",
      description = "Summary of the content, maybe generated, from metadata or just the start of the text")
  public String title(@GraphQLContext final BaleenDocument document, @GraphQLArgument(name = "size",
      description = "Maximum length of summary", defaultValue = "256") final int size) {


    String summary = null;

    final Map<String, Object> metadata = document.getMetadata();
    if (metadata != null) {
      summary = lookInMetadata(metadata);
    }

    if (isEmpty(summary)) {
      summary = document.getContent();
    }

    if (summary == null || isEmpty(summary)) {
      return "";
    }

    return summary.length() < size - ELLIPSES.length() ? summary
        : (summary.substring(0, Math.max(0, size - ELLIPSES.length())) + ELLIPSES);

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
