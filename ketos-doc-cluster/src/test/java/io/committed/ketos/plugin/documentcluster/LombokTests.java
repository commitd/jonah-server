package io.committed.ketos.plugin.documentcluster;

import org.junit.Test;
import io.committed.invest.test.LombokDataTestSupport;
import io.committed.ketos.plugin.documentcluster.data.Clusters;

public class LombokTests {

  @Test
  public void testLombok() {
    final LombokDataTestSupport mt = new LombokDataTestSupport();

    mt.testPackage(Clusters.class);
  }
}
