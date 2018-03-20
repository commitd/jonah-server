package io.committed.invest.core;

import org.junit.Test;
import io.committed.invest.test.LombokDataTestSupport;
import io.committed.ketos.core.config.ElasticsearchCorpus;
import io.committed.ketos.core.config.KetosCoreSettings;
import io.committed.ketos.core.config.MongoCorpus;
import io.committed.ketos.core.config.MongoFeedback;

public class LombokTest {

  @Test
  public void testLombok() {
    final LombokDataTestSupport mt = new LombokDataTestSupport();

    mt.testClasses(MongoFeedback.class, MongoCorpus.class, KetosCoreSettings.class, ElasticsearchCorpus.class);
  }

}
