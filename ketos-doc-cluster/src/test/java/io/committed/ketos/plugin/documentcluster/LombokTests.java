package io.committed.ketos.plugin.documentcluster;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import io.committed.invest.test.LombokDataTestSupport;
import io.committed.ketos.plugin.documentcluster.data.Clusters;
import io.committed.ketos.plugin.documentcluster.data.Topic;

public class LombokTests {

  @Test
  public void testLombok() {
    final LombokDataTestSupport mt = new LombokDataTestSupport();

    mt.testPackage(Clusters.class);



    assertThat(new Topic().getSize()).isEqualTo(0);

  }

}
