package io.committed.ketos.test.common.providers.baleen;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DocumentProvider;

public abstract class AbstractDocumentProviderTest {

  @Test
  public void testGetById() {
    BaleenDocument doc =
        getDocumentProvider().getById("575f6e573aaa400bd69f6c282ced6c81969aff20abe96be4ac8989f1f74ef55b").block();
    assertEquals("575f6e573aaa400bd69f6c282ced6c81969aff20abe96be4ac8989f1f74ef55b", doc.getId());
  }

  @Test
  public void testCount() {
    long count = getDocumentProvider().count().block();
    assertEquals(4, count);
  }

  public abstract DocumentProvider getDocumentProvider();

}
