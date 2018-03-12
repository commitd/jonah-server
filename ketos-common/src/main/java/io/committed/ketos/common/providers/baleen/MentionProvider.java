package io.committed.ketos.common.providers.baleen;

import java.util.List;
import java.util.Optional;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.input.MentionProbe;
import io.committed.ketos.common.graphql.intermediate.MentionSearchResult;
import io.committed.ketos.common.graphql.output.MentionSearch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MentionProvider extends DataProvider {

  // TODO: Need to consider if mentions are globally unique of not by id? Do these mentions need to be
  // scoped to a document (eg getId(doc, mentionId)

  @Override
  default String getProviderType() {
    return "MentionProvider";
  }

  Flux<BaleenMention> getByDocument(final BaleenDocument document);

  Mono<BaleenMention> getById(final String id);

  Flux<BaleenMention> getAll(final int offset, final int size);

  default Flux<BaleenMention> getByExample(final MentionProbe probe, final int offset, final int size) {
    return search(MentionSearch.builder().mentionFilter(probe.toFilter()).build(), offset, size)
        .getResults();
  }

  Flux<TermBin> countByField(Optional<MentionFilter> filter, List<String> path, final int size);


  MentionSearchResult search(final MentionSearch search,
      final int offset,
      final int size);

  Mono<Long> count();

}
