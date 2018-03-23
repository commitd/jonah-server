package io.committed.ketos.test.common.providers.baleen;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.ketos.common.providers.baleen.MetadataProvider;

public abstract class AbstractMetadataProviderTest {

  public abstract MetadataProvider getMetadataProvider();

  @Test
  public void testCountByKey() {
    List<TermBin> metadata = getMetadataProvider().countByKey(Optional.of("Content-Type"), 1000).collectList().block();
    assertEquals(1, metadata.size());
    assertEquals("Content-Type", metadata.get(0).getTerm());
    assertEquals(4, metadata.get(0).getCount());
  }

  @Test
  public void testCountByValue() {
    List<TermBin> metadata =
        getMetadataProvider().countByValue(Optional.of("Content-Type"), 1000).collectList().block();
    assertEquals(1, metadata.size());
    assertEquals("application/pdf", metadata.get(0).getTerm());
    assertEquals(4, metadata.get(0).getCount());
  }

}
