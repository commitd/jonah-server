package io.committed.ketos.graphql.baleen.document;

import static org.springframework.util.StringUtils.isEmpty;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentMetadata;
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

    final List<BaleenDocumentMetadata> metadata = document.getMetadata();
    if (metadata != null) {
      summary = lookInMetadata(document).orElse(null);
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


  private Optional<String> lookInMetadata(final BaleenDocument document) {

    final Optional<String> optional = document.findSingleFromMetadata("summary");
    if (optional.isPresent() && !isEmpty(optional.get())) {
      return optional;
    }

    final String keywords = document.findAllFromMetadata("keywords")
        .collect(Collectors.joining("; "));
    if (!isEmpty(keywords)) {
      return Optional.of(keywords);
    }

    return Optional.empty();
  }

}
