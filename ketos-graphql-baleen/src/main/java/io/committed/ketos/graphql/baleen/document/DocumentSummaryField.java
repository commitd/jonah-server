package io.committed.ketos.graphql.baleen.document;

import static org.springframework.util.StringUtils.isEmpty;
import java.util.Optional;
import java.util.stream.Collectors;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.ketos.common.data.BaleenDocument;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Mono;

@GraphQLService
public class DocumentSummaryField {

  private static final String ELLIPSES = "...";

  @GraphQLQuery(name = "summary",
      description = "Summary of the content, maybe generated, from metadata or just the start of the text")
  public String title(@GraphQLContext final BaleenDocument document, @GraphQLArgument(name = "size",
      description = "Maximum length of summary", defaultValue = "256") final int size) {


    String summary = null;

    summary = lookInMetadata(document).orElse(null);

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

    final Mono<String> mono = document.findSingleFromMetadata("summary");
    // TODO: Rewrite with mono throughout

    final Optional<String> optional = mono.blockOptional();
    if (optional.isPresent() && !isEmpty(optional.get())) {
      return optional;
    }

    final Mono<String> keywords = document.findAllFromMetadata("keywords")
        .collect(Collectors.joining("; "));
    final Optional<String> joinedKeywords = keywords.blockOptional();

    if (joinedKeywords.isPresent() && !isEmpty(joinedKeywords.get())) {
      return joinedKeywords;
    }

    return Optional.empty();
  }

}
