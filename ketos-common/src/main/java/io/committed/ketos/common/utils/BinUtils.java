package io.committed.ketos.common.utils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.invest.core.constants.TimeInterval;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TermCount;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.analytic.Timeline;

/**
 * Helper functions to fuse multiple data provider aggregation output
 *
 * <p>These do not address granularity issues - (eg merging a histogram of a month with one for a
 * week).
 */
public final class BinUtils {

  private BinUtils() {
    // Singleton
  }

  /**
   * Join term bins.
   *
   * @param flux the flux
   * @return joined string
   */
  public static Mono<TermCount> joinTermBins(final Flux<TermBin> flux) {
    return flux.groupBy(TermBin::getTerm)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TermBin(g.key(), l)))
        .sort()
        .collectList()
        .map(TermCount::new);
  }

  /**
   * Merge time bins to a single timeline
   *
   * <p>Note this will not convert timebins of different intervals. All timebins must be of the same
   * interval size.
   *
   * @param flux the flux
   * @param interval the interval
   * @return joined timeline
   */
  public static Mono<Timeline> joinTimeBins(final Flux<TimeBin> flux, final TimeInterval interval) {
    return flux.groupBy(TimeBin::getTs)
        .flatMap(g -> g.reduce(0L, (a, b) -> a + b.getCount()).map(l -> new TimeBin(g.key(), l)))
        .sort()
        .collectList()
        .map(l -> new Timeline(interval, l));
  }
}
