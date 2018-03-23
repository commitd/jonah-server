package io.committed.ketos.test.common.providers.baleen;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.committed.invest.extensions.data.providers.CrudDataProvider;

public abstract class AbstractCrudDataProviderTest<R, T> {

  public abstract CrudDataProvider<R, T> getDataProvider();

  public abstract R getDeleteReference();

  public abstract T getSaveItem();

  @Test
  public void testSave() {
    boolean result = getDataProvider().save(getSaveItem());
    assertTrue(result);
  }

  @Test
  public void testDelete() {
    boolean delete = getDataProvider().delete(getDeleteReference());
    assertTrue(delete);
  }

}
