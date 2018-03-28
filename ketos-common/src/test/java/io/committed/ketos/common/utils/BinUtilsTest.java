package io.committed.ketos.common.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import reactor.core.publisher.Flux;

import io.committed.invest.core.constants.TimeInterval;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TermCount;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.core.dto.analytic.Timeline;

public class BinUtilsTest {

  @Test
  public void testJoinTermBins() {
    final List<TermBin> bins =
        Arrays.asList(new TermBin("a", 10), new TermBin("a", 20), new TermBin("c", 5));
    final TermCount joined = BinUtils.joinTermBins(Flux.fromIterable(bins)).block();

    assertThat(joined.getBins())
        .containsExactlyInAnyOrder(new TermBin("a", 30), new TermBin("c", 5));
  }

  @Test
  public void testJoinTimeBins() {
    final List<TimeBin> bins =
        Arrays.asList(
            new TimeBin(Instant.ofEpochMilli(0), 90),
            new TimeBin(Instant.ofEpochMilli(10), 5),
            new TimeBin(Instant.ofEpochMilli(10), 6));
    final Timeline joined =
        BinUtils.joinTimeBins(Flux.fromIterable(bins), TimeInterval.MONTH).block();

    assertThat(joined.getBins())
        .containsExactlyInAnyOrder(
            new TimeBin(Instant.ofEpochMilli(0), 90), new TimeBin(Instant.ofEpochMilli(10), 11));
  }
}
