package io.committed.ketos.plugins.data.mongo;

import org.junit.Test;

import io.committed.invest.test.LombokDataTestSupport;
import io.committed.ketos.plugins.data.mongo.data.CountOutcome;

public class LombokTest {

  @Test
  public void testLombok() {
    final LombokDataTestSupport mt = new LombokDataTestSupport();

    mt.testClasses(CountOutcome.class);
  }
}
