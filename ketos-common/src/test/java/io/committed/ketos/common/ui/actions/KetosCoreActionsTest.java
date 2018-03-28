package io.committed.ketos.common.ui.actions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class KetosCoreActionsTest {

  @Test
  public void test() {
    // This test is just a warning that you'll break the UI
    assertThat(KetosCoreActions.CORPUS_VIEW).isEqualTo("corpus.view");
  }

  @Test
  public void testMake() {
    assertThat(KetosCoreActions.make("a", "b")).isEqualTo("a.b");
    assertThat(KetosCoreActions.make("a", "b", "c")).isEqualTo("a.b.c");
    assertThat(KetosCoreActions.make("a")).isEqualTo("a");
  }
}
