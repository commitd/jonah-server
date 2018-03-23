package io.committed.ketos.test.common.providers.baleen;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DocumentProvider;

public abstract class AbstractDocumentProviderTest {

  @Test
  public void testGetById() {
    BaleenDocument doc =
        getDocumentProvider().getById("402da4330a8ac77d0b250fe35c43a98b76c7876cbc00bba8df95832cefac1c4d").block();
    assertEquals("402da4330a8ac77d0b250fe35c43a98b76c7876cbc00bba8df95832cefac1c4d", doc.getId());
    assertEquals(" \n \n", doc.getContent());
  }

  public abstract DocumentProvider getDocumentProvider();

}
