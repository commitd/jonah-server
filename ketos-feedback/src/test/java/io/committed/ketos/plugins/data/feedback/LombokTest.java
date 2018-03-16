package io.committed.ketos.plugins.data.feedback;

import org.junit.Test;
import io.committed.invest.test.LombokDataTestSupport;
import io.committed.ketos.plugins.data.feedback.data.Feedback;

public class LombokTest {

  @Test
  public void testLombok() {
    final LombokDataTestSupport mt = new LombokDataTestSupport();

    mt.testClass(Feedback.class);
  }

}
