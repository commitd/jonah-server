package io.committed.ketos.common.utils;

import java.util.Collections;
import java.util.List;
import com.google.common.base.Splitter;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TermCount;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.analytic.Timeline;
import io.committed.invest.core.dto.constants.TimeInterval;
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
        .sort()
        .collectList()
        .map(TermCount::new);
  }

  /**
   * Merge time bines to a single timeline
   *
   * Note this will not convert timebins of different intervals. All timebins must be of the same
   * interval size.
   *
   * @param flux
   * @param interval
   * @return
   */
  public static Mono<Timeline> joinTimeBins(final Flux<TimeBin> flux, final TimeInterval interval) {
    return flux.groupBy(TimeBin::getTs)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount())
            .map(l -> new TimeBin(g.key(), l)))
        .sort()
        .collectList()
        .map(l -> new Timeline(interval, l));
  }
}
