package io.committed.ketos.data.jpa;

import org.junit.Test;
import io.committed.invest.test.LombokDataTestSupport;
import io.committed.ketos.data.jpa.dao.JpaDocument;
import io.committed.ketos.data.jpa.dao.JpaDocumentMetadata;
import io.committed.ketos.data.jpa.dao.JpaEntity;

public class LombokTests {

  @Test
  public void testLombok() {
    final LombokDataTestSupport mt = new LombokDataTestSupport();

    mt.testClasses(JpaDocument.class, JpaDocumentMetadata.class, JpaEntity.class);

  }

}
