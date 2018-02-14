package io.committed.ketos.common.utils;

import java.util.Collections;
import java.util.List;
import com.google.common.base.Splitter;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TermCount;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class FieldUtils {

  // Fixed limit here to avoid some crazy depth, when in reality it'll be 2.
  private static final Splitter FIELD_SPLITTER = Splitter.on(".").trimResults().omitEmptyStrings().limit(5);

  private FieldUtils() {
    // Singleton
  }

  public static List<String> fieldSplitter(final String field) {
    if (field == null) {
      return Collections.emptyList();
    }
    return FIELD_SPLITTER.splitToList(field);
  }

  public static Mono<TermCount> joinTermBins(final Flux<TermBin> flux) {
    return flux.groupBy(TermBin::getTerm)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TermBin(g.key(), l)))
        .collectList().map(TermCount::new);
  }
}
